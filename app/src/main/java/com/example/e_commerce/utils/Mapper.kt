package com.example.e_commerce.utils

interface Mapper<Entity, DomainModel> {
    fun mapFromEntity(entity: Entity): DomainModel
    fun mapToEntity(domainModel: DomainModel): Entity
}