package fungames

import org.codetome.zircon.api.Size
import org.codetome.zircon.api.builder.TerminalBuilder

fun main(args: Array<String>) {

    var terminal = TerminalBuilder.newBuilder()
            .initialTerminalSize(Size.of(32,16))
            .buildTerminal()

    terminal.flush()
}