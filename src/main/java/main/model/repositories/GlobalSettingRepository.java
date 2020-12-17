package main.model.repositories;

import main.model.entities.GlobalSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingRepository extends CrudRepository<GlobalSetting, Integer> {

    @Query(value = "SELECT value FROM global_settings WHERE code = 'STATISTICS_IS_PUBLIC'",
           nativeQuery = true)
    String findPermissions();
}
