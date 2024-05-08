package com.wisedaimyo.chesstraining.chess.model.board

import kotlin.random.Random

enum class Position {
    a1, a2, a3, a4, a5, a6, a7, a8,
    b1, b2, b3, b4, b5, b6, b7, b8,
    c1, c2, c3, c4, c5, c6, c7, c8,
    d1, d2, d3, d4, d5, d6, d7, d8,
    e1, e2, e3, e4, e5, e6, e7, e8,
    f1, f2, f3, f4, f5, f6, f7, f8,
    g1, g2, g3, g4, g5, g6, g7, g8,
    h1, h2, h3, h4, h5, h6, h7, h8;

    val file: Int = ordinal / 8 + 1

    val fileAsLetter: Char =
        toString()[0]

    val rank: Int = ordinal % 8 + 1

    companion object {

        fun randomBlack(): Position {
            var file: Int
            var rank: Int
            do {
                file = Random.nextInt(1, 9)
                rank = Random.nextInt(1, 9)
            } while ((file + rank) % 2 != 0)
            return Position.from(file, rank)
        }

        fun random(): Position {
            val file = Random.nextInt(1, 9)
            val rank = Random.nextInt(1, 9)
            return from(file, rank)
        }

        fun from(file: Int, rank: Int): Position {
            validate(file, rank)

            val idx = idx(file, rank)

            return entries[idx]
        }

        fun fromString(position: String): Position {
            val file = position[0].toLowerCase() - 'a' + 1
            val rank = position[1].toString().toInt()
            return from(file, rank)
        }
    }
}