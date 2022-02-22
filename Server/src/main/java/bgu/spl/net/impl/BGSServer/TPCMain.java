package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.habetnikim.HabetnikimStreetCode;
import bgu.spl.net.impl.habetnikim.MoaBetProtcol;
import bgu.spl.net.srv.BaseServer;

public class TPCMain {
    public static void main(String[] args){
        BaseServer<String> bserver = BaseServer.threadPerClient(
                Integer.decode(args[0]).intValue(),
                ()-> new MoaBetProtcol(),
                ()-> new HabetnikimStreetCode());
        bserver.serve();
    }
}
