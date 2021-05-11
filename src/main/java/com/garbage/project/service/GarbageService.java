package com.garbage.project.service;

import com.garbage.project.model.GarbageBin;
import com.garbage.project.param.GarbageQueryParam;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GarbageService {

    List<GarbageBin> getAll();

    /**
     * 按照一般属性查询垃圾箱
     * */
    Page<GarbageBin> list(GarbageQueryParam param);

    long listCount(GarbageQueryParam param);

    GarbageBin add(GarbageBin garbageBin);

    /**
     * 单个垃圾箱清理使用modify，多个垃圾箱使用cleanGBs
     * */
    boolean modifyBin(GarbageBin garbageBin);

    boolean deleteBin(String id);

    GarbageBin getBinById(String id);

    int cleanGarbages(List<GarbageBin> garbageBins);

}
