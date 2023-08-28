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

import io.smallrye.mutiny.Uni;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;


/**
 * Mostly used as a facade so all controllers have a single point of entry
 *
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
public interface ClinicService {

    Uni<Pet> findPetById(Integer id) ;
    Uni<List<Pet>> findAllPets() ;
    Uni<Void> savePet(Pet pet) ;
    Uni<Void> deletePet(Pet pet) ;

    Uni<List<Visit>> findVisitsByPetId(Integer petId);
	Uni<Visit> findVisitById(Integer visitId) ;
    Uni<List<Visit>> findAllVisits() ;
	Uni<Void> saveVisit(Visit visit) ;
    Uni<Void> deleteVisit(Visit visit) ;

    Uni<Vet> findVetById(Integer id) ;
	Collection<Vet> findVets() ;
    Uni<List<Vet>> findAllVets() ;
	Uni<Void> saveVet(Vet vet) ;
	Uni<Void> deleteVet(Vet vet) ;

    Uni<Owner> findOwnerById(Integer id) ;
    Uni<List<Owner>> findAllOwners() ;
	Uni<Void> saveOwner(Owner owner) ;
	Uni<Void> deleteOwner(Owner owner) ;
    Uni<List<Owner>>  findOwnerByLastName(String lastName) ;

    Uni<PetType> findPetTypeById(Integer petTypeId);
    Uni<List<PetType>> findAllPetTypes() ;
    Uni<List<PetType>> findPetTypes() ;
	Uni<Void> savePetType(PetType petType) ;
	Uni<Void> deletePetType(PetType petType) ;

    Uni<Specialty> findSpecialtyById(Integer specialtyId);
    Uni<List<Specialty>> findAllSpecialties() ;
    Uni<Void> saveSpecialty(Specialty specialty) ;
    Uni<Void> deleteSpecialty(Specialty specialty) ;

}
