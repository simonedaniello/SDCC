package all.endpoint.dao;

import org.hibernate.annotations.Entity;
import org.springframework.data.annotation.Id;


/**
 * Author : Simone D'Aniello
 * Date :  06/03/2018.
 */

public class CrossroadDao {

    private String id;
    private String address;

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }
}
