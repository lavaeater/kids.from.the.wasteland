package story.conversation

open class ConversationStep(
    val key:String,
    val antagonistLines: Iterable<String>,
    val conversationRoutes: Iterable<ConversationRoute>)

class EmptyStep:ConversationStep("empty", emptyList(), emptyList())