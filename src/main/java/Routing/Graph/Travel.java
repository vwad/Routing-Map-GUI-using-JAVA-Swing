package Routing.Graph;

import java.util.List;
import java.util.ArrayList;

public class Travel {
    private List<TravelInformation> travelOrder;
    private TravelInformation walk;
    public Travel()
    {
        travelOrder = new ArrayList<>();
    }
    public void appendTravel(TravelInformation travel)
    {
            travelOrder.add(travel);
    }

    /**
     * Binary search of the faster travel available, assuming times are in seconds, to calculate weight. It's universally equal to travelInformation arrival_time - time
     * @param time time seconds after which we return travel
     * @return travel if there exists, otherwise returns null
     */
    public TravelInformation getTravelInfoAfterTime(double time)
    {
        if(travelOrder.isEmpty()||travelOrder.getLast().getDepartureTime()<time){
            if(walk!=null) {
                walk.setDepartureTime(time);
                return walk;
            }
            return null;
        }

        int start = 0;
        int end = travelOrder.size();
        while(start<end)
        {
            int middle = (end+start)/2;
            if(travelOrder.get(middle).getDepartureTime()<time)
            {
                start = middle+1;
            }
            else if(travelOrder.get(middle).getDepartureTime()==time)
                break;
            else
            {
                end = middle-1;
            }
        }
        TravelInformation result = travelOrder.get(start);
        if(walk != null) {
            walk.setDepartureTime(time);
            if(walk.getArrivalTime()<result.getArrivalTime()||result.getDepartureTime()<time) {
                return walk;
            }
        }
        if(result.getDepartureTime()<time) {
            return null;
        }

        return result;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for(TravelInformation travelInformation : travelOrder)
        {
            builder.append(travelInformation.toString());
        }
        builder.append(walk);
        return builder.toString();
    }

    public TravelInformation getWalk(){
        return walk;
    }

    public  List<TravelInformation> getTravelOrder(){
        return travelOrder;
    }

    public void setWalk(TravelInformation walk)
    {
        this.walk = walk;
    }

    public void setTravelOrder(List<TravelInformation> travelOrder)
    {
        this.travelOrder = travelOrder;
    }
}
