//
// Created by shoham on 20/02/2022.
//
#include "../include/connectionHandler.h"
#include "../include/Task.h"

Task::Task(bool &shutdown, ConnectionHandler &conn):bye(shutdown), conn_(conn) {}

void Task::operator()() {
    while (!bye) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        int len = line.length();
        std::string msg = conn_.msgFormat(line);
        if(msg != "BAD"){
            if (!conn_.sendLine(msg)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            // connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
            std::cout << "Sent " << len + 1 << " bytes to server" << std::endl;}
        else std::cout<<"not a vallid command!"<<std::endl;
    }
}
