package com.mvc.forrest.dao.storage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mvc.forrest.service.domain.Search;
import com.mvc.forrest.service.domain.Storage;


@Mapper
public interface StorageDAO {
	
	
	void addStorage(Storage storage) throws Exception;
	
	Storage getStorage(String tranNo) throws Exception;
	
	void updateStorage(Storage storage) throws Exception;
	
	List<Storage> getStorageList(Map<String,Object> map) throws Exception;
	
	List<Storage> getStorageListForAdmin(Search search) throws Exception;
	
	List<Storage> getExpiredStorageList() throws Exception;
	
	int getTotalCount(Map<String,Object> map) throws Exception;	
}
