package in.trident.crdr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import in.trident.crdr.entities.CompanySelection;

public interface CompSelectionRepo extends JpaRepository<CompanySelection, Long> {

}
