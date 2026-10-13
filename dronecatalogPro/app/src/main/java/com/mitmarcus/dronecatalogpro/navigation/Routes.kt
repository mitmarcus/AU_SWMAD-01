package com.mitmarcus.dronecatalogpro.navigation

import kotlinx.serialization.Serializable

// Type-safe Compose Navigation routes

@Serializable object SignIn
@Serializable object SignUp

@Serializable object Home
@Serializable data class PartList(val category: String)
@Serializable data class PartDetail(val partId: Int)

@Serializable object Builds
@Serializable data class BuildDetail(val buildId: String)

@Serializable object Spots
@Serializable data class SpotDetail(val spotId: String)
