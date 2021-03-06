package controllers

 import io.javalin.Context
import models.Activity
import models.Friend
import models.Location
import models.User

class PacemakerRestService {
    val pacemaker = PacemakerAPI()

    fun listUsers(ctx: Context) {
        ctx.json(pacemaker.users)
    }

    fun createUser(ctx: Context) {
        val user = ctx.bodyAsClass(User::class.java)
        val newUser = pacemaker.createUser(user.firstname, user.lastname, user.email, user.password)
        ctx.json(newUser)
    }

    fun deleteUsers(ctx: Context) {
        pacemaker.deleteUsers()
        ctx.status(204)
    }

    fun getActivity(ctx: Context) {
        // val userId: String? = ctx.param("id")
        val activityId: String? = ctx.param("activityId")
        val activity = pacemaker.getActivity(activityId!!)
        if (activity != null) {
            ctx.json(activity)
        } else {
            ctx.status(404)
        }
    }

    fun follow(ctx: Context) {
        val id: String? = ctx.param("id")
        val user = pacemaker.getUser(id!!)
        val email: String? = ctx.param("email")
        val friend = pacemaker.getUserByEmail(email!!)
        var newFriend = Friend(friend!!);
        ctx.json(pacemaker.follow(user!!, newFriend!!))
    }

    fun getActivities(ctx: Context) {
        val id: String? = ctx.param("id")
        val user = pacemaker.getUser(id!!)
        if (user != null) {
            val activities = user.activities
            val activitiesList = ArrayList(activities.values)
            ctx.json(activitiesList)
        } else {
            ctx.status(404)
        }
    }

    fun createActivity(ctx: Context) {
        val id: String? = ctx.param("id")
        val user = pacemaker.getUser(id!!)
        if (user != null) {
            val activity = ctx.bodyAsClass(Activity::class.java)
            val newActivity = pacemaker.createActivity(user.id, activity.type, activity.location, activity.distance)
            ctx.json(newActivity!!)
        } else {
            ctx.status(404)
        }
    }

    fun deleteActivites(ctx: Context) {
        val id: String? = ctx.param("id")
        pacemaker.deleteActivities(id!!)
        ctx.status(204)
    }

    fun createLocation(ctx: Context) {
        val id: String? = ctx.param("id")
        val user = pacemaker.getUser(id!!)
        val activityId: String? = ctx.param("activityId")
        val activity = pacemaker.getActivity(activityId!!)
        if (user != null) {
            if (activity != null) {
                val location = ctx.bodyAsClass(Location::class.java)
                val newLocation = pacemaker.createLocation(user.id, activity.id, location.latitude, location.longitude)
                ctx.json(newLocation!!)
            }
        } else {
            ctx.status(404)
        }
    }

    fun getLocation(ctx: Context) {
        val id: String? = ctx.param("id")
        val user = pacemaker.getUser(id!!)
        val activityId: String? = ctx.param("activityId")
        val activity = pacemaker.getActivity(activityId!!)
        if (user != null) {
            if (activity != null) {
                val location = ArrayList(activity.route)
                ctx.json(location)
            }
        } else {
            ctx.status(404)
        }
    }

    fun getActivityReport(ctx: Context) {
        val id: String? = ctx.param("id")
        val user = pacemaker.getUser(id!!)
        if (user != null) {
            val activities = user.activities
            val activitiesList = ArrayList(activities.values)
            val sortedActivityList = activitiesList.sortedWith(compareBy({ it.type }, { it.type }))
            ctx.json(sortedActivityList)
        } else {
            ctx.status(404)
        }
    }

    fun listFriends(ctx: Context) {
        val id: String? = ctx.param("id")
        val user = pacemaker.getUser(id!!)
        if (user != null) {
            if (user.friends.isEmpty()) {
                ctx.status(404)
            } else {
                ctx.json(user.friends)
            }
        } else {
            ctx.status(404)
        }
    }

    fun getFriendActivityReport(ctx: Context) {
        val id: String? = ctx.param("id")
        val email: String? = ctx.param("email")
        val user = pacemaker.getUser(id!!)
        if (user != null) {
            if (user.friends.isEmpty()) {
                ctx.status(404)
            } else {
                user.friends.forEach { friend ->
                    if (friend.friend.email == email) {
                        val friendActivityList = ArrayList(friend.friend.activities.values)
                        ctx.json(friendActivityList)
                    }
                }
            }
        }
    }

    fun sendMessage(ctx: Context) {
        val id: String? = ctx.param("id")
        val email: String? = ctx.param("email")
        val message: String? = ctx.bodyAsClass(String::class.java)
        val user = pacemaker.getUser(id!!)
        user?.friends?.forEach { friend ->
            if (friend.friend.email == email) {
                friend.friend.messages.add(message!!)
                ctx.json(message)
            }
        }
    }

    fun listMessages(ctx: Context) {
        val id: String? = ctx.param("id")
        val user = pacemaker.getUser(id!!)
        if (user != null) {
            ctx.json(user.messages)
        } else {
            ctx.status(404)
        }
    }

    fun messageAll(ctx: Context) {
        val id: String? = ctx.param("id")
        val user = pacemaker.getUser(id!!)
        val message: String? = ctx.bodyAsClass(String::class.java)
        user?.friends?.forEach { friend ->
            friend.friend.messages.add(message!!)
        }
    }

    fun distanceLeaderBoard(ctx: Context) {
        ctx.json(pacemaker.getDistanceLeaderBoard())
    }

    fun distanceLeaderBoardByType(ctx: Context) {
        val type: String? = ctx.param("type")
        ctx.json(pacemaker.getDistanceLeaderBoardByType(type!!))
    }

    fun locationLeaderBoard(ctx: Context) {
        val location: String? = ctx.param("location")
        ctx.json(pacemaker.getLocationLeaderBoard(location!!))
    }

    fun unfollow(ctx: Context) {
        val id: String? = ctx.param("id")
        val user = pacemaker.getUser(id!!)
        val email: String? = ctx.param("email")
        val friend = pacemaker.getUserByEmail(email!!)
        user?.friends?.forEachIndexed({ i, f ->
            if (f.friend.id == friend?.id) {
                user.friends.removeAt(i)
            }
        })
        ctx.json(user!!)
    }
}