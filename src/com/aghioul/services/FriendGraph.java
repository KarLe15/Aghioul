package com.aghioul.services;


import com.aghioul.analytics.AghioulNetwork;
import org.bson.Document;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;


import java.util.*;

public class FriendGraph {

    public static void main(String[] args) {
        test2();
    }


    public static Graph init(String key, String username){
        Graph g = new SingleGraph("");
        Document result = AghioulNetwork.getNeighborhood(key, username);
        ArrayList<Document> JSNodes = (ArrayList<Document>) result.get("nodes");
        ArrayList<Document> edges = (ArrayList<Document>) result.get("edges");
        ArrayList<Node> nodes = new ArrayList<Node>();
        for (Document d : JSNodes) {
            nodes.add(g.addNode(d.getString("id")) );
        }
        int i = 0;
        for (Document d : edges) {
            String source = d.getString("source");
            String dest = d.getString("target");
            g.addEdge(""+i,source, dest, true);
            i++;
        }
        Node a = nodes.get(0);
        System.out.println(getSuggestion(a));
        return g;
    }

    public static Document getSuggestions(String key, String username){
        Graph g = init(key,username);
        Node me = g.getNode(username);
        List<String> suggestions = getSuggestion(me);
        Document res = new Document();
        res.append("status","accepted").append("suggestions",suggestions);
        return res;
    }



    public static void test2(){
        System.out.println(getSuggestions("lol","karle15"));
    }


    public static Set<Node> getListFriends(Node node){
        Set<Node> friends = new HashSet<>();

        for (Iterator<? extends Node> it = node.getNeighborNodeIterator(); it.hasNext(); ) {
            Node n = it.next();

            if(n.hasEdgeFrom(node.getId())){
                friends.add(n);
            }
        }
        return friends;
    }

    public static Set<Node> getFollowers(Node node){
        Set<Node> friends = new HashSet<>();
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
        Set<Node> common = new HashSet<>(listA);
        common.retainAll(listB);
        return common;
    }

    public static Set<Node> getNewFriends(Node from, Node toMe){
        Set<Node> listA = getListFriends(from);
        Set<Node> listB = getListFriends(toMe);
        Set<Node> common = new HashSet<>(listA);
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
}