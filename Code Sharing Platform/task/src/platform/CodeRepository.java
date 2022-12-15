package platform;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

//JPA repository with key CodeWrapper function to work with the database in the Service API
@Repository
public interface CodeRepository extends JpaRepository<CodeWrapper,Long> {
    CodeWrapper findCodeWrapperByUuid(UUID Uuid);
    void delete(CodeWrapper codeWrapper);
    List<CodeWrapper> findCodeWrapperByUnrestricted(Boolean Unrestricted);
    @Override
    List<CodeWrapper> findAll();
}
