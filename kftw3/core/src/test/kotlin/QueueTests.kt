
import com.badlogic.gdx.utils.Queue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParcelTests {

	@Test
	fun parcel_addComponent_hasComponent() {
		//arrange
		val p = Parcel()
		val c = BroadCastContent()

		//act
		p.addContent(c)

		//assert
		assertTrue { p.hasContent<BroadCastContent>() }
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
	val contents = mutableSetOf<ParcelContent>()

	fun addContent(c: ParcelContent) {
		contents.add(c)
	}

	inline fun <reified T: ParcelContent> getContent(): T? {
		return contents.firstOrNull { it is T} as T?
	}

	inline fun <reified T: ParcelContent> hasContent(): Boolean {
		return contents.firstOrNull { it is T } != null
	}
}

class BroadCastContent: ParcelContent()

abstract class ParcelContent

abstract class ParcelProcessor {
	abstract fun processParcel(p: Parcel)
}

abstract class GenericParcelProcessor<T: ParcelContent>: ParcelProcessor()

class ParcelCentral(initialSize: Int = 10) {
	val q = Queue<Parcel>(initialSize)

	fun postParcel(p: Parcel) {
		q.addLast(p)
	}

	fun addProcessor(processor: ParcelProcessor) {

	}
}
