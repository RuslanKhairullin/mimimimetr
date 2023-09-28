package ru.ruslankhayrullin.mimimimetr.persistence.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ruslankhayrullin.mimimimetr.model.persistence.CatRateInfo;
import ru.ruslankhayrullin.mimimimetr.persistence.repository.CatRateInfoRepository;

/**
 * Service for interaction with CRUD repository9
 */
@Service
public class CatRateInfoService {
    private final CatRateInfoRepository catRateInfoRepository;

    @Autowired
    public CatRateInfoService(CatRateInfoRepository catRateInfoRepository) {
        this.catRateInfoRepository = catRateInfoRepository;
    }

    public List<CatRateInfo> getAllResults() {
        var catRateInfos = new ArrayList<CatRateInfo>();
        catRateInfoRepository.findAll().forEach(catRateInfos::add);
        return catRateInfos;
    }

    @Transactional
    public void saveChat(CatRateInfo catRateInfo) {
        catRateInfoRepository.save(catRateInfo);
    }

    @Transactional
    public void incrementCatPoints(String catName) {
        var catInfo = catRateInfoRepository.findCatRateInfoByNameCat(catName);
        catInfo.setPoints(catInfo.getPoints() + 1);
        catRateInfoRepository.save(catInfo);
    }

    public List<CatRateInfo> getAllCatsOrderByPoints() {
       return getAllResults().stream().sorted(Comparator.comparingInt(CatRateInfo::getPoints).reversed()).collect(Collectors.toList());
    }

    public List<CatRateInfo> getRandomAllCats() {
        var allCats = getAllResults();
        Collections.shuffle(allCats);
        return allCats;
    }
}
