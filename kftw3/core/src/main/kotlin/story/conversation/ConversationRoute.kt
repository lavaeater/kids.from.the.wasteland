package story.conversation

class ConversationRoute(val key:String,
                        val text:String = "Ja",
                        val routeEmotion: RouteEmotion = RouteEmotion.Positive,
                        val routeType: RouteType = RouteType.Continue) {
}