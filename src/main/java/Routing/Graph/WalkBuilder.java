package Routing.Graph;

import Utils.Distance.CoordinatesCalculator;
import Utils.Time.WalkTimeCalculator;

import java.util.List;

public class WalkBuilder {

    public void addWalkingTravels(double[] dxAndDy, StopGraph graph, double walkingDistance, List<Node> stops) {
        boolean travelToEnd = false;
        boolean travelFromStart = false;
        for (int i = 0; i < stops.size(); i++) {
            Node from = stops.get(i);
            double fromLat = from.getLat();
            double fromLon = from.getLon();
            for (int j = i + 1; j < stops.size(); j++) {
                Node to = stops.get(j);
                double toLat = to.getLat();
                double toLon = to.getLon();
                double weight = getWeightWithinBounds(dxAndDy, fromLat, fromLon, toLat, toLon, walkingDistance);

                if (weight < 0)
                    continue;

                if(to.getId().equals("end"))
                    travelToEnd = true;

                if(from.getId().equals("start"))
                    travelFromStart = true;

                addWalkFromTo(from,to,weight,graph);
                addWalkFromTo(to,from,weight,graph);
            }
        }

        if(!travelFromStart)
            addWalksFromOrTo(graph,walkingDistance,stops,graph.getStop("start"),true);
        if(!travelToEnd)
            addWalksFromOrTo(graph,walkingDistance,stops,graph.getStop("end"),false);
    }

    public static double getWeightWithinBounds(double[] dxAndDy,  double fromLat, double fromLon, double toLat, double toLon, double walkingDistance)
    {
        if(fromLat-dxAndDy[1]>toLat || fromLat+dxAndDy[1]<toLat
                || fromLon-dxAndDy[0]>toLon || fromLon+dxAndDy[0]<toLon)
            return -1;
        double calculatedDistanceKM = CoordinatesCalculator.calculateDistanceKM(fromLat, fromLon, toLat, toLon);
        if(calculatedDistanceKM>walkingDistance)
            return -1;
        return calculatedDistanceKM;
    }

    public void addWalkFromStartToEnd(StopGraph graph, double walkingDistance, List<Node> stops)
    {
        addWalksFromOrTo(graph,walkingDistance,stops,graph.getStop("start"),true);
        addWalksFromOrTo(graph,walkingDistance,stops,graph.getStop("end"),false);
    }

    private void addWalksFromOrTo(StopGraph graph, double walkingDistance, List<Node> stops, Node node,boolean from)
    {
        if(node==null)
            return;

        double smallestWeight = Integer.MAX_VALUE;
        Node closestNode = new HolderNode();
        for (Node stop : stops) {
            if(stop.equals(node))
                continue;
            double stopLat = stop.getLat();
            double stopLon = stop.getLon();
            double weight = CoordinatesCalculator.calculateDistanceKM(node.getLat(),node.getLon(),stopLat,stopLon);

            if(smallestWeight > weight)
            {
                smallestWeight = weight;
                closestNode = stop;
            }

            if (weight > walkingDistance)
                continue;

            if(from) {
                addWalkFromTo(node,stop,weight,graph);
            }
            else
            {
                addWalkFromTo(stop,node,weight,graph);
            }
        }

        if(from) {
            addWalkFromTo(node,closestNode,smallestWeight,graph);
        }
        else
        {
            addWalkFromTo(closestNode,node,smallestWeight,graph);
        }

    }

    private void addWalkFromTo(Node from, Node to, double weight, StopGraph graph)
    {
        double time = WalkTimeCalculator.distanceToWalkingSeconds(weight);
        WalkingTravelInformation walk = new WalkingTravelInformation(time, from, to);
        graph.addWalk(from.getId(), to.getId(), walk);
    }


    public double addStartEndWalk(StopGraph graph, double startLat, double startLon, double endLat, double endLon )
    {
        double calculatedDistanceKM = CoordinatesCalculator.calculateDistanceKM(startLat, startLon, endLat, endLon);
        double weight = WalkTimeCalculator.distanceToWalkingSeconds(calculatedDistanceKM);
        graph.addWalk("start", "end", new WalkingTravelInformation(weight,graph.getStop("start"),graph.getStop("end") ));
        return weight;
    }
}
