package connectfour
import java.lang.Exception

val win = intArrayOf(0, 0)

class Game(val rows: Int = 6, val cols: Int = 7) {
    val playF = MutableList(rows + 2) { MutableList(cols * 2 + 1) { " " } }
    private var inx = 0
    private var finish = false

    init {
        for (i in 1..rows) {
            for (j in 1..cols * 2 + 1 step 2) {
                playF[i][j - 1] = "║"
                if (inx < cols) playF[0][j] = (++inx).toString()
            }
        }
        playF[rows + 1][0] = "╚".also { inx = 0 }
        repeat(cols - 1) { playF[rows + 1][++inx] = "═╩" }.also { playF[rows + 1][++inx] = "═╝" }
        draw()
    }

    fun draw() {
        for (col in playF) println(col.joinToString(""))
    }

    private fun checkTotal(total: String, players: List<String>): Boolean {
        if (total == "oooo") {
            println("Player ${players[0]} won").also { win[0] = win[0] + 2 }.also { finish = true }
                .also { return true }
        } else if (total == "****") {
            println("Player ${players[1]} won").also { win[1] = win[1] + 2 }.also { finish = true }
                .also { return true }
        } else return false
    }

    fun checkResult(players: List<String>): String {
        var total = ""
        loop@ for (cn in 1 until playF[0].lastIndex step 2) {
            for (ln in 1 until playF.lastIndex) {
                if (cn + 6 <= playF[0].lastIndex - 1) {
                    total = playF[ln][cn] + playF[ln][cn + 2] + playF[ln][cn + 4] + playF[ln][cn + 6]
                    if (checkTotal(total, players)) break@loop
                }
                if (ln + 3 <= playF.lastIndex - 1) {
                    total = playF[ln][cn] + playF[ln + 1][cn] + playF[ln + 2][cn] + playF[ln + 3][cn]
                    if (checkTotal(total, players)) break@loop
                }
                if (ln + 3 <= playF.lastIndex - 1 && cn + 6 <= playF[0].lastIndex - 1) {
                    total = playF[ln][cn] + playF[ln + 1][cn + 2] + playF[ln + 2][cn + 4] + playF[ln + 3][cn + 6]
                    if (checkTotal(total, players)) break@loop
                }
                if (ln - 3 >= 1 && cn + 6 <= playF[0].lastIndex - 1) {
                    total = playF[ln][cn] + playF[ln - 1][cn + 2] + playF[ln - 2][cn + 4] + playF[ln - 3][cn + 6]
                    if (checkTotal(total, players)) break@loop
                }
            }
        }
        if (!finish) {
            var numberOfSpaces = 0
            for (line in 1 until playF.lastIndex) {
                numberOfSpaces += playF[line].filter { it == " " }.size
            }
            if (numberOfSpaces == 0) println("It is a draw").also { total = "end" }.also { win[0] = win[0] + 1 }
                .also { win[1] = win[1] + 1 }
        } else total = "end"
        return total
    }
}

fun start() {
    println("Connect Four\nFirst player's name:")
    val players = listOf(readln().also { println("Second player's name:") }, readln())
    val map = mapOf(players[0] to "o", players[1] to "*")
    var game: Game
    var games = 1
    var currentGame = 1
    var gameSelection = ""
    var answer = ""
    var move = 0

    fun rep(rows: Int, cols: Int) {
        while (true) {
            println("Do you want to play single or multiple games?\nFor a single game, input 1 or press Enter\nInput a number of games:")
            gameSelection = readln()
            try {
                if (gameSelection.isNotEmpty()) games = gameSelection.toInt()
                if (games == 0) throw Exception()
                break
            } catch (e: Exception) {
                println("Invalid input")
            }
        }
        println("${players[0]} VS ${players[1]}\n$rows X $cols board ${if (games == 1) "\nSingle game" else "\nTotal $games games\nGame #$currentGame"}")
    }
    while (true) {
        println("Set the board dimensions (Rows x Columns)\nPress Enter for default (6 x 7)")
        val board = readln().trim()
        val arr = board.split("[xX]".toRegex())

        if (board.isEmpty()) {
            rep(6, 7).also { game = Game() }
            break
        } else if (board.matches("\\d+\\s*[xX]\\s*\\d+".toRegex())) {
            val rows = arr[0].trim().toInt()
            val cols = arr[1].trim().toInt()
            if (rows !in 5..9) {
                println("Board rows should be from 5 to 9")
            } else if (cols !in 5..9) {
                println("Board columns should be from 5 to 9")
            } else {
                rep(rows, cols).also { game = Game(rows, cols) }
                break
            }
        } else println("Invalid input")
    }
    while (!answer.equals("end", true) && currentGame <= games) {
        println("${players[move]}'s turn:")
        try {
            answer = readln()
            if (answer.equals("end", true)) break
            if (answer.toInt() !in 1..game.cols) {
                println("The column number is out of range (1 - ${game.cols})")
                continue
            } else {
                var full = true
                for (line in game.rows downTo 1) {
                    if (game.playF[line][answer.toInt() * 2 - 1] == " ") {
                        game.playF[line][answer.toInt() * 2 - 1] = map[players[move]]!!
                        full = false.also { move = if (move == 0) 1 else 0 }
                        game.draw().also { answer = game.checkResult(players) }
                        break
                    }
                }
                if (full) println("Column ${answer.toInt()} is full")
            }
        } catch (e: Exception) {
            println("Incorrect column number")
        }
        if (currentGame < games && answer == "end") {
            currentGame++.also { answer = "" }
            println("Score\n${players[0]}: ${win[0]} ${players[1]}: ${win[1]}\nGame #$currentGame")
            game = Game(game.rows, game.cols)
        } else if (games > 1 && currentGame == games && answer == "end")
            println("Score\n${players[0]}: ${win[0]} ${players[1]}: ${win[1]}")
    }
    println("Game over!")
}

fun main() = start()