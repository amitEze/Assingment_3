package bgu.spl.net.impl.habetnikim;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {
    ConcurrentHashMap<Integer, ConnectionHandler> clientsHandlers=new ConcurrentHashMap<Integer,ConnectionHandler>(); //for active client *may not be online
    ConcurrentLinkedQueue<Betnik> users = new ConcurrentLinkedQueue<Betnik>();
    private static ConnectionsImpl me = null;
    private static AtomicInteger clientId = new AtomicInteger(0);

    private ConnectionsImpl(){}

    public static ConnectionsImpl getInstance(){
        if(me==null)
            me=new ConnectionsImpl();
        return me;
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if(clientsHandlers.get(connectionId)!=null){
            clientsHandlers.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) { //should send also to clients that has not logged in
        for(Integer id: clientsHandlers.keySet()){
            clientsHandlers.get(id).send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        if(clientsHandlers.get(connectionId)!=null)
            clientsHandlers.remove(connectionId);
    }

    public void addUser(Betnik u)
    {
        users.add(u);
    }

    public void addHandelr(ConnectionHandler handler){
        clientsHandlers.put(clientId.getAndIncrement(),handler);
    }


    public ConcurrentLinkedQueue<Betnik> getUsers() {
        return users;
    }

    public ConnectionHandler getCHandler(int id){
        return clientsHandlers.get(id);
    }
}
