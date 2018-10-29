
import com.badlogic.gdx.utils.Queue
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParcelTests {

	@Test
	fun parcel_addComponent_hasComponent() {
		//arrange
		val p = Parcel()
		val c = BroadCastPayload()

		//act
		p.addPayload(c)

		//assert
		assertTrue { p.hasPayload<BroadCastPayload>() }
	}

	@Test
	fun parcel_addTwoComponents_hasBothComponent() {
		//arrange
		val p = Parcel()
		val c = BroadCastPayload()
		val e = EventPayload()

		//act
		p.addPayload(c)
		p.addPayload(e)


		//assert
		assertTrue { p.hasPayload<BroadCastPayload>() }
		assertTrue { p.hasPayloadType(e::class) }
	}
}

class QueueTests {
	@Test
	fun postParcel_QueueContainsOneParcel() {
		//Arrange
		/*
		Simple first, parcels are created as needed,
		garbage collector handles everything for us.
		No need to get fancy with a pool, just yet.

		LibGdx has ring buffer queue implementation
		so we will obviously use that one... which means that
		our queue is DONE, no need to test enqueuing etc,
		we need to get right down to brass tacks and test
		components and handling of parcels.

		For this we need a ParcelCentral. This will contain the
		q and will send Parcels to interested parties...
		 */
		val p = Parcel()
		val pc = ParcelCentral()

		//Act
		pc.postParcel(p)

		//Assert
		assertEquals(1, pc.q.size)
	}
}

class Parcel {
	val payloads = mutableSetOf<ParcelPayload>()
	private val payLoadClasses = mutableSetOf<KClass<out ParcelPayload>>()

	fun addPayload(c: ParcelPayload) {
		payloads.add(c)
		payLoadClasses.add(c::class)
	}

	inline fun <reified T: ParcelPayload> getContent(): T? {
		return payloads.firstOrNull { it is T} as T?
	}

	inline fun <reified T: ParcelPayload> hasPayload(): Boolean {
		return payloads.firstOrNull { it is T } != null
	}

	fun hasPayloadType(t: KClass<out ParcelPayload>): Boolean {
		return payLoadClasses.contains(t)
	}
}

class BroadCastPayload: ParcelPayload()
class EventPayload: ParcelPayload()

abstract class ParcelPayload

abstract class ParcelProcessor(private val forPayloadsOfType: List<KClass<*>>, val priority: Int = 0){
	/*
	How will we keep track of listeners?

	That's the most intereseting part of this conundrum.

	I actually just thought up some intriguing concept for how to send and receive
	messages, but how will we decide what "entities" receive which messages?

	The point would be that entities have "capabilities" that somehow correspond to
	different types of payloads.

	We must back to the drawing board and add our different entities to the mix, to the
	sketch of this entire system. Nice.

	I can imagine that a processParcel-method also might take all entities? Or a filtered
	list of entities that are interested in that type of payload? Or?

	We need a more comprehensive list of what we want this engine to dooo...
	 */

	fun canProcessParcel(p: Parcel) : Boolean {
		return forPayloadsOfType.all { p.hasPayloadType(it as KClass<ParcelPayload>) }
	}

	abstract fun processParcel(p: Parcel)
}

class AttentionProcessor:ParcelProcessor(listOf(EventPayload::class)) {



	override fun processParcel(p: Parcel) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}

class ParcelCentral(initialSize: Int = 10) {
	val q = Queue<Parcel>(initialSize)
	private val processors = mutableSetOf<ParcelProcessor>()

	fun postParcel(p: Parcel) {
		q.addLast(p)
	}

	fun processNextParcel() {
		val parcel = q.removeFirst()
		processors
				.filter { it.canProcessParcel(parcel) }
				.sortedBy { it.priority }
				.forEach { it.processParcel(parcel) }
	}

	fun addProcessor(processor: ParcelProcessor) {
		processors.add(processor)
	}
}
