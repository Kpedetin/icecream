package com.blackbox.dao.repository;

import com.blackbox.dao.model.Tools;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ToolsRepository extends MongoRepository<Tools, String> {

}
