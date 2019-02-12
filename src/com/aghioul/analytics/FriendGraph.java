package com.aghioul.analytics;


import com.aghioul.DB.Database;
import com.aghioul.analytics.AghioulNetwork;
import com.aghioul.tools.ErrorAghioul;

import org.bson.Document;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Dijkstra.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FriendGraph {

    public static void main(String[] args) {
        test2();
    }


    public static Graph init(String key, String username){
    	Connection c = null;
		try {
			c = Database.getMySQLConnection();
		} catch (SQLException e){
		    e.printStackTrace();
		    return null;
		}
		
        Graph g = new SingleGraph(username+"-graph");
        g.setAutoCreate(true);
        PreparedStatement preparedStatement = null;
        try {
			
			preparedStatement = c.prepareStatement(AghioulNetwork.get2ndNeighborhoodEdges);
			for(int i=1; i<7; i++)
				preparedStatement.setString(i, username);
			
			ResultSet res = preparedStatement.executeQuery();
			//ajouter les aretes du graphe
			HashSet<String> nodes = new HashSet<String>();
			while(res.next()) {
	
				if(!nodes.contains(res.getString("u1")))
					g.addNode(res.getString("u1"));
				if(!nodes.contains(res.getString("u2")))
					g.addNode(res.getString("u2"));
				nodes.add(res.getString("u1"));
				nodes.add(res.getString("u2"));
				g.addEdge(res.getString("u1")+"-"+res.getString("u2"), res.getString("u1"), res.getString("u2"), true);
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return g;
    }

//
//    public static Document getSuggestions(String key, String username){
//        Graph g = init(key,username);
//        Node me = g.getNode(username);
//
//        /*calcul des plus courts chemins avec djikstra*/
//        Dijkstra dijkstra = new Dijkstra(null, null, null);
//        dijkstra.setSource(g.getNode(username));
// 		dijkstra.init(g);
// 		dijkstra.compute();
//
// 		/*algorithme ultra naïf*/
// 		//pour tous les noeuds à distance 2
// 		for(Node n : g) {
// 			double l = dijkstra.getPathLength(n);
// 			System.out.println(username+"----"+n.getId()+" "+l);
//
// 			if (n.getId() == username)
//				continue;
//
// 			if(l == Double.POSITIVE_INFINITY) {
// 				if(n.hasEdgeBetween(me)) {
//
// 				}
// 				else {
//
// 				}
// 			} else{
//
// 			}
//
// 		}
//
//        return ErrorAghioul.serviceAccepted().append("suggestions","lol");
//    }
//


    public static void test2(){
        System.out.println(getSuggestions("lol","karle15"));
    }


    public static Set<Node> getListFriends(Node node){
        Set<Node> friends = new HashSet<Node>();
        for (Iterator<? extends Node> it = node.getNeighborNodeIterator(); it.hasNext(); ) {
            Node n = it.next();
            if(n.hasEdgeFrom(node.getId())){
                friends.add(n);
            }
        }
        return friends;
    }

    public static Set<Node> getFollowers(Node node){
        Set<Node> friends = new HashSet<Node>();
        for (Iterator<? extends Node> it = node.getNeighborNodeIterator(); it.hasNext(); ) {
            Node n = it.next();
            if(n.hasEdgeToward(node.getId())){
                friends.add(n);
            }
        }
        return friends;
    }

    public static Set<Node> getCommonFriend(Node a , Node b){
        Set<Node> listA = getListFriends(a);
        Set<Node> listB = getListFriends(b);
        Set<Node> common = new HashSet<Node>(listA);
        common.retainAll(listB);
        return common;
    }

    public static Set<Node> getNewFriends(Node from, Node toMe){
        Set<Node> listA = getListFriends(from);
        Set<Node> listB = getListFriends(toMe);
        Set<Node> common = new HashSet<Node>(listA);
        common.removeAll(listB);
        common.remove(toMe);
        return common;
    }

    public static Set<Node> getNewFriendsFromCommonInterrest(Node from, Node toMe){
        Set<Node> possiblity = getFollowers(from);
        Set<Node> myFriends = getListFriends(toMe);
        possiblity.removeAll(myFriends);
        possiblity.remove(toMe);
        return possiblity;
    }



    public static List<String> getSuggestion(Node me){
        Map<String,Integer> nodeSuggestionsFriends = new HashMap<>();
        Map<String,Integer> nodeSuggestionsInterest = new HashMap<>();
        Set<Node> friends = getListFriends(me);
        for (Node node : friends) {
            Set<Node> newFriends = getNewFriends(node,me);
            int value = 0;
            for (Node n: newFriends) {
                String nom = n.getId();
                value = ( (nodeSuggestionsFriends.get(nom)== null) ?  0 : nodeSuggestionsFriends.get(nom));
                nodeSuggestionsFriends.put(nom,++value);
            }


            Set<Node> newInterest = getNewFriendsFromCommonInterrest(node,me);
            value = 0;
            for (Node n: newInterest) {
                String nom = n.getId();
                value = ( (nodeSuggestionsInterest.get(nom)== null) ?  0 : nodeSuggestionsInterest.get(nom));
                nodeSuggestionsInterest.put(nom,++value);
            }


        }

        ////////
        List<String> suggestions = new ArrayList<String>(nodeSuggestionsFriends.keySet());
        suggestions.addAll(nodeSuggestionsInterest.keySet());

        suggestions.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int freq1 = (nodeSuggestionsFriends.get(o1) == null ? nodeSuggestionsInterest.get(o1) : nodeSuggestionsFriends.get(o1) );
                int freq2 = (nodeSuggestionsFriends.get(o2) == null ? nodeSuggestionsInterest.get(o2) : nodeSuggestionsFriends.get(o2) );
                if(freq1>freq2){
                    return 1;
                }else if( freq1< freq2){
                    return -1;
                }
                return 0;
            }
        });

        return suggestions;
    }


    public static Document getSuggestions(String key, String username){
        Graph g = init(key,username);
        Node me = g.getNode(username);
        List<String> suggestions = getSuggestion(me);
        Document res = new Document();
        res.append("status","accepted").append("suggestions",suggestions);
        return res;
    }
}
