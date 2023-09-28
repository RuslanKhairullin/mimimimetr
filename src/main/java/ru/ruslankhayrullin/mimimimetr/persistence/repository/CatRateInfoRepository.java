package ru.ruslankhayrullin.mimimimetr.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import ru.ruslankhayrullin.mimimimetr.model.persistence.CatRateInfo;

/**
 * Repository for interactions with db
 */
public interface CatRateInfoRepository extends CrudRepository<CatRateInfo, Long> {

    CatRateInfo findCatRateInfoByNameCat(String catName);

}
