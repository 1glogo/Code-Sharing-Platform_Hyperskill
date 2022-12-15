package platform;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface CodeRepository extends JpaRepository<CodeWrapper,Long> {

    CodeWrapper findCodeWrapperById(Long Id);
    CodeWrapper findCodeWrapperByUuid(UUID Uuid);

    CodeWrapper findCodeWrapperByCode(String Code);

    void delete(CodeWrapper codeWrapper);

    List<CodeWrapper> findCodeWrapperByUnrestricted(Boolean Unrestricted);
    @Override
    List<CodeWrapper> findAll();


}
