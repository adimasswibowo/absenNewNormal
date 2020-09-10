package com.nnam.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.nnam.project.model.Params;

public interface ParamsRepository extends CrudRepository<Params, Integer>{
	
	Optional<Params> findByTypeAndCode (@Param ("type") String type, @Param ("code") String code);
	List<Params> findByType (@Param ("type") String type);


}
