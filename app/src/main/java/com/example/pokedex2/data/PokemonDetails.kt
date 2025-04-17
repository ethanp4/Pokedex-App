package com.example.pokedex2.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PokemonDetails(
    val id: Int,
    val name: String,
    val base_experience: Int,
    val height: Int,
    val weight: Int,
    val stats: List<Stat>,
    val sprites: Sprites,
    val abilities: List<Ability>,
    val moves: List<Move>,
    val types: List<Type>,
    @Json(name = "is_default") val isDefault: Boolean,
    val species: Species,
    val order: Int? = null,
    val cries: Cries? = null
)

///////stats/////////
@JsonClass(generateAdapter = true)
data class Stat(
    val base_stat: Int,
    val effort: Int,
    val stat: StatInfo
)

@JsonClass(generateAdapter = true)
data class StatInfo(
    val name: String,
    val url: String
)

/////////sprites//////////
@JsonClass(generateAdapter = true)
data class Sprites(
    @Json(name = "back_default") val backDefault: String?,
    @Json(name = "back_shiny") val backShiny: String?,
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "front_shiny") val frontShiny: String?,
    val other: OtherSprites?
)

@JsonClass(generateAdapter = true)
data class OtherSprites(
    @Json(name = "official-artwork") val officialArtwork: OfficialArtwork?,
    val home: HomeSprites?,
    @Json(name = "dream_world") val dreamWorld: DreamWorld?
)

@JsonClass(generateAdapter = true)
data class OfficialArtwork(
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "front_shiny") val frontShiny: String?
)

@JsonClass(generateAdapter = true)
data class HomeSprites(
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "front_shiny") val frontShiny: String?
)

@JsonClass(generateAdapter = true)
data class DreamWorld(
    @Json(name = "front_default") val frontDefault: String?
)

///////abilities////////
@JsonClass(generateAdapter = true)
data class Ability(
    val ability: AbilityInfo,
    @Json(name = "is_hidden") val isHidden: Boolean,
    val slot: Int
)

@JsonClass(generateAdapter = true)
data class AbilityInfo(
    val name: String,
    val url: String
)

//////moves//////
@JsonClass(generateAdapter = true)
data class Move(
    val move: MoveInfo,
    @Json(name = "version_group_details") val versionGroupDetails: List<VersionGroupDetail>
)

@JsonClass(generateAdapter = true)
data class MoveInfo(
    val name: String,
    val url: String
)

@JsonClass(generateAdapter = true)
data class VersionGroupDetail(
    @Json(name = "level_learned_at") val levelLearnedAt: Int,
    @Json(name = "move_learn_method") val moveLearnMethod: MoveLearnMethod,
    @Json(name = "version_group") val versionGroup: VersionGroup
)

@JsonClass(generateAdapter = true)
data class MoveLearnMethod(
    val name: String,
    val url: String
)

@JsonClass(generateAdapter = true)
data class VersionGroup(
    val name: String,
    val url: String
)

//////////types////////
@JsonClass(generateAdapter = true)
data class Type(
    val slot: Int,
    val type: TypeInfo
)

@JsonClass(generateAdapter = true)
data class TypeInfo(
    val name: String,
    val url: String
)

////////species///////
@JsonClass(generateAdapter = true)
data class Species(
    val name: String,
    val url: String
)

////////cries////////
@JsonClass(generateAdapter = true)
data class Cries(
    val latest: String?,
    val legacy: String?
)