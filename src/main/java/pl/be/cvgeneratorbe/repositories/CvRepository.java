package pl.be.cvgeneratorbe.repositories;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import pl.be.cvgeneratorbe.entity.CvEntity;

import java.util.List;

@EnableScan
public interface CvRepository extends CrudRepository<CvEntity, String> {
    List<CvEntity> findByFullName(String nameAndSurname);
}
