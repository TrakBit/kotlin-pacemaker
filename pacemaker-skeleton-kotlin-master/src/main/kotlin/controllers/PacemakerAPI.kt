package controllers

import models.*

class PacemakerAPI {

    var userIndex = hashMapOf<String, User>()
    var emailIndex = hashMapOf<String, User>()
    var activitiesIndex = hashMapOf<String, Activity>()
    var users = userIndex.values

    fun createUser(firstName: String, lastName: String, email: String, password: String): User {
        var user = User(firstName, lastName, email, password)
        userIndex[user.id] = user
        emailIndex[user.email] = user
        return user
    }

    fun deleteUsers() {
        userIndex.clear()
        emailIndex.clear()
    }

    fun getUser(id: String) = userIndex[id]
    fun getUserByEmail(email: String) = emailIndex[email]

    fun createActivity(id: String, type: String, location: String, distance: Float): Activity? {
        var activity: Activity? = null
        var user = userIndex.get(id)
        if (user != null) {
            activity = Activity(type, location, distance)
            user.activities[activity.id] = activity
            activitiesIndex[activity.id] = activity
        }
        return activity;
    }

    fun createLocation(id: String, activityId: String, latitude: Double, longitude: Double) {
        var user = userIndex.get(id)
        if (user != null) {
            var activity = activitiesIndex.get(activityId)
            if (activity != null) {
                val location = Location(latitude, longitude)
                activity.route.add(location)
            }
        }
    }

    fun getActivity(id: String): Activity? {
        return activitiesIndex[id]
    }

    fun deleteActivities(id: String) {
        require(userIndex[id] != null)
        var user = userIndex.get(id)
        if (user != null) {
            for ((u, activity) in user.activities) {
                activitiesIndex.remove(activity.id)
            }
            user.activities.clear()
        }
    }

    fun follow(user: User, friend: Friend) {
        var newFriend = true
        user.friends.forEach({ f ->
            if (f.friend.email == friend.friend.email) newFriend = false
        })
        if (newFriend) user.friends.add(friend)
    }

    fun getDistanceLeaderBoard(): List<LeaderBoard> {
        val leaderBoardList = arrayListOf<LeaderBoard>()
        users.forEach({
            val email = it.email
            var score = 0.0F
            it.activities.forEach { activity ->
                score += activity.value.distance
            }
            val leaderBoard = LeaderBoard(email, score)
            leaderBoardList.add(leaderBoard)
        })
        return leaderBoardList.sortedWith(compareByDescending({it.score}))
    }

    fun getLocationLeaderBoard(location: String): ArrayList<LeaderBoard> {
        val leaderBoardList = arrayListOf<LeaderBoard>()
        users.forEach({
            it.activities.forEach({ activity ->
                var email: String
                var score = 0.0F
                if (activity.value.location == location) {
                    score += activity.value.distance
                    email = it.email
                    val leaderBoard = LeaderBoard(email, score)
                    leaderBoardList.add(leaderBoard)
                }
            })
        })
        return leaderBoardList
    }

}