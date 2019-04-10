package mx.tec.jobbify_enterprise.beans;

/*
* Base class for Job object
*
* The location variable should be change to GoogleMaps API
*/

public class Job {
    private String title;
    private String desc;
    private Integer salary;
    private String type; // Full time or Intern
    private String dateToApply; //date up to when apply
    private String address;
    private String contactEmail;
    private String contactPhone;
    private String location; // should be a 'Location' object from GoogleMaps API
    private String publishedBy; //Enterprise that publishes it

    public Job() {
    }
}
