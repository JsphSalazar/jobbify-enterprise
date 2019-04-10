package mx.tec.jobbify_enterprise.beans;

/*
* Base class for Job object
*
* The location variable should be change to GoogleMaps API
*/

public class Job {
    private String title;
    private String desc;
    private Integer salaryLowerBound;
    private Integer salaryUpperBound;
    private String type; // Full time or Intern
    private String durationOfContract;
    private String address;
    private String contactEmail;
    private String contactPhone;
    private String location; // should be a 'Location' object from GoogleMaps API
    private String publishedBy; //Enterprise that publishes it

    public Job() {
    }

    public Job(String title, String desc, Integer salaryLowerBound, Integer salaryUpperBound, String type, String durationOfContract, String address, String contactEmail, String contactPhone, String location, String publishedBy) {
        this.title = title;
        this.desc = desc;
        this.salaryLowerBound = salaryLowerBound;
        this.salaryUpperBound = salaryUpperBound;
        this.type = type;
        this.durationOfContract = durationOfContract;
        this.address = address;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.location = location;
        this.publishedBy = publishedBy;
    }
}
