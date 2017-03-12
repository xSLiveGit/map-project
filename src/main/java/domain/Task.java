package domain;

/**
 * Created by Sergiu on 1/3/2017.
 */
public class Task implements HasID<Integer>{
    private String type;
    private Integer duration;
    private Integer id;
    private String description;

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    /*
        Parameters constructor
        Input :
            id - int
            type - String
            duration - int
         */
    public Task(Integer Id, String type, Integer duration,String description){
        this.id = Id;
        this.type = type;
        this.duration = duration;
        this.description = description;
    }

    /*
    Default constructor
     */

    public Task(){
        this(-1,"",-1,"");
    }

    //type getter
    public String getType(){
        return this.type;
    }

    //duration getter
    public Integer getDuration(){
        return this.duration;
    }


    //type setter
    public void setType(String type){
        this.type = type;
    }

    //duration setter
    public void setDuration(Integer duration){
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o){
        if(null == o)   return false;
        if(o instanceof Task) {
            Task s = (Task) o;
            return (this.duration.equals(s.duration)) && (this.type.equals( s.type)) && (this.description.equals(s.description));
        }
        return false;
    }

    public String toString(){
        return "| Type: " + this.type + " | Duration: " + this.duration + " | Description:" + this.description;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode(){
        return (id.hashCode() & duration.hashCode() & type.hashCode() & description.hashCode());
    }

}
