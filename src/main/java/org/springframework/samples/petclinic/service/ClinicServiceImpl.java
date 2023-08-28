/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.service;

import java.util.Collection;
import java.util.List;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.jpa.JpaOwnerRepository;
import org.springframework.samples.petclinic.repository.jpa.JpaPetRepository;
import org.springframework.samples.petclinic.repository.jpa.JpaPetTypeRepository;
import org.springframework.samples.petclinic.repository.jpa.JpaSpecialtyRepository;
import org.springframework.samples.petclinic.repository.jpa.JpaVetRepository;
import org.springframework.samples.petclinic.repository.jpa.JpaVisitRepository;

import io.quarkus.cache.CacheResult;

/**
 * Mostly used as a facade for all Petclinic controllers
 * Also a placeholder for @Transactional and @Cacheable annotations
 *
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@ApplicationScoped
public class ClinicServiceImpl implements ClinicService {

    public JpaPetRepository petRepository;
    public JpaVetRepository vetRepository;
    public JpaOwnerRepository ownerRepository;
    public JpaVisitRepository visitRepository;
    public JpaSpecialtyRepository specialtyRepository;
	public JpaPetTypeRepository petTypeRepository;

	@Inject
     public ClinicServiceImpl(
       		 JpaPetRepository petRepository,
    		 JpaVetRepository vetRepository,
    		 JpaOwnerRepository ownerRepository,
    		 JpaVisitRepository visitRepository,
    		 JpaSpecialtyRepository specialtyRepository,
			 JpaPetTypeRepository petTypeRepository) {
        this.petRepository = petRepository;
        this.vetRepository = vetRepository;
        this.ownerRepository = ownerRepository;
        this.visitRepository = visitRepository;
        this.specialtyRepository = specialtyRepository;
		this.petTypeRepository = petTypeRepository;
    }

	@Override
    @WithTransaction
    @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)
	public Uni<List<Pet>> findAllPets()  {
		return petRepository.listAll();
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)
	public Uni<Void> deletePet(Pet pet)  {
		return petRepository.deleteObject(pet);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)
	public Uni<Visit> findVisitById(Integer visitId)  {
        return visitRepository.findById(visitId);
    }

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)
	public Uni<List<Visit>> findAllVisits()  {
		return visitRepository.listAll();
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Void> deleteVisit(Visit visit)  {
        //currently based on hibernate mapping model, error is  Unable to perform un-delete for instance org.springframework.samples.petclinic.model.Visit
		return visitRepository.delete(visit);
        //return visitRepository.deleteObject(visit);
	}

    @Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Vet> findVetById(Integer id)  {
        return vetRepository.findById(id);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)
	public Uni<List<Vet>> findAllVets()  {
		return vetRepository.listAll();
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Void> saveVet(Vet vet)  {
		return vetRepository.save(vet);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Void> deleteVet(Vet vet)  {
		return vetRepository.delete(vet);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<List<Owner>> findAllOwners()  {
		return ownerRepository.listAll();
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Void> deleteOwner(Owner owner)  {
		return ownerRepository.delete(owner);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<PetType> findPetTypeById(Integer petTypeId) {
        return petTypeRepository.findById(petTypeId);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<List<PetType>> findAllPetTypes()  {
		return petTypeRepository.listAll();
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Void> savePetType(PetType petType)  {
		return petTypeRepository.save(petType);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Void> deletePetType(PetType petType)  {
		return petTypeRepository.deleteObject(petType);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Specialty> findSpecialtyById(Integer specialtyId) {
        return specialtyRepository.findById(specialtyId);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<List<Specialty>> findAllSpecialties()  {
		return specialtyRepository.listAll();
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Void> saveSpecialty(Specialty specialty)  {
		return specialtyRepository.save(specialty);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Void> deleteSpecialty(Specialty specialty)  {
		return specialtyRepository.deleteWithVetAssign(specialty);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)
	public Uni<List<PetType>> findPetTypes()  {
		return petTypeRepository.findPetTypes();
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Owner> findOwnerById(Integer id)  {
        return ownerRepository.findByIdLeftJoin(id);
	}

	@Override
	@WithTransaction
    @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)
	public Uni<Pet> findPetById(Integer id)  {
        return petRepository.findById(id);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)
	public Uni<Void> savePet(Pet pet) {
        //return petRepository.persist(pet).replaceWithVoid();
		return petRepository.save(pet);

	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<Void> saveVisit(Visit visit)  {
		return visitRepository.save(visit);

	}

	//@Override
	@WithTransaction
    @CacheResult(cacheName = "vets")
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<List<Owner>> findOwners()  {
		return ownerRepository.listAll();
	}

	@Override
	@WithTransaction
    @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)
	public Uni<Void> saveOwner(Owner owner)  {
		return ownerRepository.save(owner);

	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<List<Owner>> findOwnerByLastName(String lastName)  {
		return ownerRepository.findByLastName(lastName);
	}

	@Override
	@WithTransaction
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Uni<List<Visit>> findVisitsByPetId(Integer petId) {
		return visitRepository.findByPetId(petId);
	}

	@Override
        @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

	public Collection<Vet> findVets() {
		// TODO Auto-generated method stub
		return null;
	}




}
