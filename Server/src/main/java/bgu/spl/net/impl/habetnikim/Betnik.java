package bgu.spl.net.impl.habetnikim;

import bgu.spl.net.srv.ConnectionHandler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Betnik {
    private String userName;
    private String passWord;
    private String bDay;
    private ConnectionHandler cHandler=null;
    private int connectionId=-1;

    ConcurrentLinkedQueue<Betnik> following = new ConcurrentLinkedQueue<Betnik>();
    ConcurrentLinkedQueue<Betnik> followers = new ConcurrentLinkedQueue<Betnik>();
    ConcurrentLinkedQueue<Betnik> BlockedBy = new ConcurrentLinkedQueue<Betnik>();
    ConcurrentLinkedQueue<String> posts = new ConcurrentLinkedQueue<String>();
    ConcurrentLinkedQueue<String> unseeNotifications = new ConcurrentLinkedQueue<String>();
    ConcurrentLinkedQueue<String> pms = new ConcurrentLinkedQueue<String>();

    public Betnik(String userName, String passWord, String bDay){
        this.userName=userName;
        this.passWord=passWord;
        this.bDay=bDay;
    }

//    public void login(ConnectionHandler ch){
//        cHandler=ch;
//    }

    public String getUserName(){return userName;}

    public String getPassWord(){return passWord;}

    public ConnectionHandler getCHandler(){return cHandler;}

    public void setHandler(ConnectionHandler c){this.cHandler=c;}

    public ConcurrentLinkedQueue<Betnik> getFollowing(){return following;}

    public  ConcurrentLinkedQueue<Betnik> getFollowers(){return followers;}

    public ConcurrentLinkedQueue<String> getUnseeNotifications(){return unseeNotifications;}

    public ConcurrentLinkedQueue<Betnik> getBlockedBy(){return BlockedBy;}

    public ConcurrentLinkedQueue<String> getPosts(){return posts;}
    public ConcurrentLinkedQueue<String> getPms(){return pms;}
    public void addPM(String pm){pms.add(pm);}
    public void setConnectionId(int id){this.connectionId=id;}
    public int getConnectionId(){return connectionId;}
    public void addNotification(String msg){unseeNotifications.add(msg);}
    public void addBlocker(Betnik blocker){
        BlockedBy.add(blocker);
        following.remove(blocker);
        followers.remove(blocker);
    }
    public short getAge(){
        LocalDate now=LocalDate.of(2022,1,14);
        Integer year=Integer.valueOf(bDay.substring(6));
        Integer month=Integer.valueOf(bDay.substring(3,5));
        Integer day=Integer.valueOf(bDay.substring(0,2));
        LocalDate bday=LocalDate.of(year,month,day);

        return (short)ChronoUnit.YEARS.between(bday,now);
    }

}
