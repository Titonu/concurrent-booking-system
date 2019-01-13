package com.tito.kafka.Repository;

import com.tito.kafka.Model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

}
