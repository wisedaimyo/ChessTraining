package com.wisedaimyo.chesstraining.main.data.models.use_case.users

data class UseCaseUsers (
    val getUsers: GetUsers,
    val getCurrentUser: GetCurrentUser,
    val addUser: AddUser,
    val deleteUser: DeleteUser,
    val changeImage: ChangeImage,
    var getUserWithName: GetUserWithName,
    var getUsersWithIds: GetUsersWithIds,
    var updateName: ChangeName,
    var resetElo: ResetElo
)