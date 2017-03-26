package pioneers.safwat.myroute;

/**
 * Created by safwa on 2/16/2017.
 */

public class RouteDB {
    int _id ;
    String _routename;
    String _routepath ;
    String _routedist ;
    String _routesize ;
    // Empty constructor
    public RouteDB(){

    }
    public RouteDB(int id, String routename, String routepath, String routedist,String routesize) {
        this._id=id;
        this._routename=routename;
        this._routepath=routepath;
        this._routedist=routedist;
        this._routesize=routesize;
    }
    public RouteDB(String routename, String routepath, String routedist,String routesize) {
        this._routename=routename;
        this._routepath=routepath;
        this._routedist=routedist;
        this._routesize=routesize;
    }
    public int getrouteid(){
        return this._id;
    }
    public String getroutename(){
        return this._routename;
    }
    public String getroutepath(){
        return this._routepath;
    }
    public String getroutedist(){ return this._routedist; }
    public String getroutesize(){ return this._routesize; }

    public void setrouteid(int routeid){ this._id = routeid;}
    public void setroutename(String routename){ this._routename = routename;}
    public void setroutepath(String routepath){ this._routepath = routepath;}
    public void setroutedist(String routedist){this._routedist = routedist;}
    public void setroutesize(String routesize){this._routesize = routesize;}

}