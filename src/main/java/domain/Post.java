package domain;

/**
 * Created by Sergiu on 1/3/2017.
 */
public class Post implements HasID<Integer> {
    private Integer id;
    private String name;
    private String type;
    private Double salary;


    /*
        Post constructor
        Input:
                id - int
                name - String
                type - String
         */
    public Post(){
        this(-1,"","full time",-1.0);
    }

    public Post(Integer id,String name, String type,Double salary){
        this.id = id;
        this.name = name;
        this.type = type;
        this.salary = salary;
    }


    /**
     *
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     *
     * @param type
     */
    public void setType(String type){
        this.type = type;
    }

    /**
     *
     * @return
     */
    public String getName(){
        return this.name;
    }

    /**
     *
     * @return
     */
    public String getType(){
        return this.type;
    }


    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }


    @Override
    public boolean equals(Object p){
        if(p == null){
            return false;
        }
        if(p instanceof Post) {
            return (this.salary.equals (((Post) p).salary)) && (this.name.equals(((Post) p).name)) && (this.type.equals(((Post) p).type));
        }
        else return false;
    }

    @Override
    public String toString(){
        return  "Name: " + this.name + " Type:" + this.type + " Salary:" + this.salary;
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
        return (id.hashCode() & name.hashCode() & type.hashCode());
    }
}
