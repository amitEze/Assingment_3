#include "../include/connectionHandler.h"
#include <algorithm>
 
using boost::asio::ip::tcp;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
 
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_){}
    
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

void shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

const std::string currentDateTime() {
    time_t     now = time(0);
    struct tm  tstruct;
    char       buf[80];
    tstruct = *localtime(&now);
    strftime(buf, sizeof(buf), "%Y-%m-%d.%X", &tstruct);

    return buf;
}

std::string ConnectionHandler::msgFormat(std::string line) {
    string msg;
    int ind=0;
    while(line[ind]!=' ')
        ind++;
    string command = line.substr(0,ind);
    if(command=="REGISTER"){
        char opC[2];
        shortToBytes((short)1,opC);
        msg=line.substr(ind+1);
        std::replace(msg.begin(),msg.end(),' ','\0');
        msg=opC[1]+msg;
        msg=opC[0]+msg;
        std::cout<< "opcode = " << opC<<endl;
        std::cout<< "msg = " << msg<<endl;
        return msg+'\0';
    }
    if(command=="LOGIN"){
        char opC[2];
        shortToBytes((short)2,opC);
        msg=line.substr(ind+1);
        std::replace(msg.begin(),msg.end(),' ','\0');
        msg=opC[1]+msg;
        msg=opC[0]+msg;
        std::cout<< opC<<endl;
        std::cout<< msg<<endl;
        return msg+'\0';
    }
    if(command=="LOGOUT"){
        short opCode=3;
        char opC[2];
        shortToBytes(opCode,opC);
        msg = opC[1] + msg;
        msg = opC[0] + msg;
        return msg+"\0";
    }
    if(command=="FOLLOW"){
        short opCode=4;
        char opC[2]=".";
        shortToBytes(opCode,opC);
        if(line[ind+1]=='0')
            return opC+'\0'+line.substr(ind+3)+'\0';
        else return opC+'a'+line.substr(ind+3)+'\0';
    }
    if(command=="POST"){
        short opCode=5;
        char opC[2]=".";
        shortToBytes(opCode,opC);
        msg=line.substr(ind+1);
        msg=opC+msg;
        return msg+'\0';
    }
    if(command=="PM"){
        short opCode=6;
        char opC[2]=".";
        shortToBytes(opCode,opC);
        msg=line.substr(ind+1);
        int ind2=msg.find(" ");
        msg.replace(ind2,1,"\0");
        msg=msg+'\0'+currentDateTime()+'\0';
        return opC+msg;
    }
    if(command=="LOGSTAT"){
        short opCode=7;
        char opC[2]=".";
        shortToBytes(opCode,opC);
        return opC;
    }
    if(command=="STAT"){
        short opCode=8;
        char opC[2]=".";
        shortToBytes(opCode,opC);
        msg=line.substr(ind+1);
        return opC+msg+'\0';
    }
    if(command=="BLOCK"){
        short opCode=12;
        char opC[2]=".";
        shortToBytes(opCode,opC);
        msg=line.substr(ind+1);
        return opC+msg+'\0';
    }
    return "not a valid command";
}

short bytesToShort(char* bytesArr){
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

string ConnectionHandler::prepareToPrint(std::string ans) {
    //char* ch=strcpy(new char[2],ans.substr(0,2).c_str());
    std::cout<<ans<<std::endl;
    char opC[2];
    opC[0]=ans[0];
    opC[1]=ans[1];
    short opCode=bytesToShort(opC);
    std::cout<<opCode<<std::endl;
    if(opCode==9){//case notification
        char* notftype=strcpy(new char[1],ans.substr(2,3).c_str());
        string info = ans.substr(3);
        int i=0;
        while(info[i] !='\0')
            i++;
        string name = info.substr(0,i);
        string content = info.substr(i+1,info.length()-2);
        if(*notftype=='\0'){//case PM
            return "NOTIFICATION PM "+name+content;
        }
        else return "NOTIFICATION POST "+name+content;// case post
    }

    if(opCode==10){//case ack
        //char* opc=strcpy(new char[2],ans.substr(2,4).c_str());
        opC[0]=ans[2];
        opC[1]=ans[3];
        short operation=bytesToShort(opC);
        std::cout<<operation<<std::endl;
        if(operation==1){
            return "ACK 01";
        }
        if(operation==2){
            return "ACK 02";
        }
        if(operation==3){
            return "\0";
        }
        if(operation==4){
            return "ACK 04"+ans.substr(4);
        }
        if(operation==5){
            return "ACK 05";
        }
        if(operation==6){
            return "ACK 06";
        }
        if(operation==7){
            char* a=strcpy(new char[2],ans.substr(4,6).c_str());
            short age=bytesToShort(a);
            string iAge= std::to_string(int(age));
            char* n=strcpy(new char[2],ans.substr(6,8).c_str());
            short numPosts=bytesToShort(n);
            string iNumPosts=std::to_string(int(numPosts));
            char* nf1=strcpy(new char[2],ans.substr(8,10).c_str());
            string numOfFollowers=std::to_string(int(bytesToShort(nf1)));
            char* nf2=strcpy(new char[2],ans.substr(10,12).c_str());
            string numOFFollowing=std::to_string(int(bytesToShort(nf2)));
            return "ACK 7 "+iAge+ " "+iNumPosts+" "+numOfFollowers+" "+numOFFollowing;
        }
        if(operation==8){
            char* a=strcpy(new char[2],ans.substr(4,6).c_str());
            short age=bytesToShort(a);
            string iAge= std::to_string(int(age));
            char* n=strcpy(new char[2],ans.substr(6,8).c_str());
            short numPosts=bytesToShort(n);
            string iNumPosts=std::to_string(int(numPosts));
            char* nf1=strcpy(new char[2],ans.substr(8,10).c_str());
            string numOfFollowers=std::to_string(int(bytesToShort(nf1)));
            char* nf2=strcpy(new char[2],ans.substr(10,12).c_str());
            string numOFFollowing=std::to_string(int(bytesToShort(nf2)));
            return "ACK 8 "+iAge+ " "+iNumPosts+" "+numOfFollowers+" "+numOFFollowing;
        }
        if(operation==12){
            return "ACK 12";
        }
    }
    if(opCode==11){//case error
        char* opc=strcpy(new char[2],ans.substr(2).c_str());
        short operation=bytesToShort(opc);
        string finalcountdown = std::to_string(int(operation));
        return "ERROR"+finalcountdown;
    }
    return "error - without id";
}


bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);			
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, ';');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, ';');
}
 
bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character. 
    // Notice that the null character is not appended to the frame string.
    try {
		do{
			getBytes(&ch, 1);
            frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.length());
	if(!result) return false;
	return sendBytes(&delimiter,1);
}
 
// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }

}