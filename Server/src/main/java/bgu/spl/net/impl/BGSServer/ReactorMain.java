package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.habetnikim.HabetnikimStreetCode;
import bgu.spl.net.impl.habetnikim.MoaBetProtcol;
import bgu.spl.net.srv.Reactor;


public class ReactorMain {
    public static void main(String[] args){
        if(args.length < 2){
            System.out.println("please enter port and num of threads");
        }
        int portSaid = Integer.parseInt(args[0]);
        int threadsNum = Integer.parseInt(args[1]);
        Reactor rserver = Reactor.reactor(threadsNum,
                portSaid,
                ()->new MoaBetProtcol(),
                ()-> new HabetnikimStreetCode());
        rserver.serve();
    }
}
