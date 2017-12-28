package controllers

import java.util.UUID;
import models.Activity
import models.Friend
import models.Location
import models.User

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
    var activity:Activity? = null
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
              val location = Location(latitude,longitude);
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
      user.activities.clear();
    }
  }

  fun follow(user: User, friend: Friend) {
      var newFriend: Boolean = true
      user.friends.forEach { f:Friend ->
        if(f.friend.email == friend.friend.email) {
          newFriend = false
        }
      }
      if (newFriend) {
        user.friends.add(friend)
      }
  }

}