package com.example.data

import com.example.logic.PlayerPiece

data class TrainingPuzzle(
    val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val boardSize: Int = 10,
    val playerPiece: PlayerPiece = PlayerPiece.X,
    val initialPieces: List<Triple<Int, Int, PlayerPiece>>,
    val correctMoves: List<Pair<Int, Int>>,
    val explanation: String,
    val rewardExp: Int = 50
)

object TrainingData {
    val PUZZLES = listOf(
        TrainingPuzzle(
            id = 1,
            title = "1. Nước Thắng Quyết Định",
            category = "Tìm nước thắng",
            description = "Bạn đang cầm quân X và đã có 4 quân liên tiếp hàng ngang. Hãy đặt quân thứ 5 để giành chiến thắng ngay lập tức!",
            boardSize = 10,
            playerPiece = PlayerPiece.X,
            initialPieces = listOf(
                Triple(4, 2, PlayerPiece.X),
                Triple(4, 3, PlayerPiece.X),
                Triple(4, 4, PlayerPiece.X),
                Triple(4, 5, PlayerPiece.X),
                Triple(3, 3, PlayerPiece.O),
                Triple(5, 4, PlayerPiece.O),
                Triple(2, 5, PlayerPiece.O)
            ),
            correctMoves = listOf(Pair(4, 1), Pair(4, 6)),
            explanation = "Đặt vào ô (5, 2) hoặc (5, 7) hoàn thành chuỗi 5 quân X liên tiếp.",
            rewardExp = 50
        ),
        TrainingPuzzle(
            id = 2,
            title = "2. Chặn Chuỗi 4 Mở Của Đối Thủ",
            category = "Chặn đối thủ",
            description = "Đối thủ O đang có 4 quân dọc liên tiếp và sắp thắng ở lượt tiếp theo. Hãy tìm và chặn ngay nước quyết định của đối thủ!",
            boardSize = 10,
            playerPiece = PlayerPiece.X,
            initialPieces = listOf(
                Triple(2, 5, PlayerPiece.O),
                Triple(3, 5, PlayerPiece.O),
                Triple(4, 5, PlayerPiece.O),
                Triple(5, 5, PlayerPiece.O),
                Triple(4, 2, PlayerPiece.X),
                Triple(4, 3, PlayerPiece.X),
                Triple(6, 4, PlayerPiece.X)
            ),
            correctMoves = listOf(Pair(1, 5), Pair(6, 5)),
            explanation = "Chặn ở ô (2, 6) hoặc (7, 6) để ngăn đối thủ O hoàn thành 5 quân dọc.",
            rewardExp = 50
        ),
        TrainingPuzzle(
            id = 3,
            title = "3. Tạo Thế Cờ 4 Quân Mở",
            category = "Tấn công",
            description = "Bạn đang có 3 quân chéo mở 2 đầu. Hãy đánh nước tạo thành chuỗi 4 quân mở 2 đầu để cầm chắc chiến thắng!",
            boardSize = 10,
            playerPiece = PlayerPiece.X,
            initialPieces = listOf(
                Triple(3, 3, PlayerPiece.X),
                Triple(4, 4, PlayerPiece.X),
                Triple(5, 5, PlayerPiece.X),
                Triple(2, 6, PlayerPiece.O),
                Triple(7, 3, PlayerPiece.O)
            ),
            correctMoves = listOf(Pair(2, 2), Pair(6, 6)),
            explanation = "Đánh vào (3, 3) hoặc (7, 7) mở rộng thành chuỗi 4 quân mở 2 đầu, đối thủ chỉ chặn được 1 đầu và bạn sẽ thắng ở đầu còn lại.",
            rewardExp = 60
        ),
        TrainingPuzzle(
            id = 4,
            title = "4. Tạo Nước Đôi (Double Threat)",
            category = "Tạo nước đôi",
            description = "Hãy đặt quân X vào vị trí giao nhau để đồng thời tạo ra 2 chuỗi 3 quân mở độc lập (nước đôi chữ L).",
            boardSize = 10,
            playerPiece = PlayerPiece.X,
            initialPieces = listOf(
                Triple(4, 2, PlayerPiece.X),
                Triple(4, 3, PlayerPiece.X),
                Triple(2, 4, PlayerPiece.X),
                Triple(3, 4, PlayerPiece.X),
                Triple(1, 1, PlayerPiece.O),
                Triple(6, 6, PlayerPiece.O)
            ),
            correctMoves = listOf(Pair(4, 4)),
            explanation = "Ô (5, 5) kết nối cả hàng ngang và cột dọc tạo thành 2 chuỗi 3 mở cùng lúc.",
            rewardExp = 75
        ),
        TrainingPuzzle(
            id = 5,
            title = "5. Chặn Bẫy Nước Đôi Của Đối Thủ",
            category = "Phòng thủ",
            description = "Đối thủ O đang rình rập tạo đòn chẻ đôi chữ V. Hãy tìm ô then chốt mà đối thủ nhắm tới để phá bẫy trước!",
            boardSize = 10,
            playerPiece = PlayerPiece.X,
            initialPieces = listOf(
                Triple(5, 2, PlayerPiece.O),
                Triple(5, 3, PlayerPiece.O),
                Triple(2, 4, PlayerPiece.O),
                Triple(3, 4, PlayerPiece.O),
                Triple(7, 7, PlayerPiece.X),
                Triple(8, 8, PlayerPiece.X)
            ),
            correctMoves = listOf(Pair(5, 4), Pair(4, 4)),
            explanation = "Chiếm ngay ô giao điểm (6, 5) hoặc (5, 5) ngăn đối thủ tạo thế cờ đôi.",
            rewardExp = 75
        ),
        TrainingPuzzle(
            id = 6,
            title = "6. Khóa Chặt Chuỗi 3 Mở",
            category = "Phòng thủ",
            description = "Đối thủ O vừa tạo 3 quân mở hàng ngang. Hãy lập tức chặn một đầu nguy hiểm nhất.",
            boardSize = 10,
            playerPiece = PlayerPiece.X,
            initialPieces = listOf(
                Triple(4, 3, PlayerPiece.O),
                Triple(4, 4, PlayerPiece.O),
                Triple(4, 5, PlayerPiece.O),
                Triple(2, 2, PlayerPiece.X),
                Triple(6, 6, PlayerPiece.X)
            ),
            correctMoves = listOf(Pair(4, 2), Pair(4, 6)),
            explanation = "Chặn ở (5, 3) hoặc (5, 7) giảm sức ép chuỗi 3 quân của đối thủ.",
            rewardExp = 50
        ),
        TrainingPuzzle(
            id = 7,
            title = "7. Tấn Công Đường Chéo Phụ",
            category = "Tấn công",
            description = "Phát hiện cơ hội tạo chuỗi 4 quân trên đường chéo phụ (anti-diagonal).",
            boardSize = 10,
            playerPiece = PlayerPiece.X,
            initialPieces = listOf(
                Triple(2, 6, PlayerPiece.X),
                Triple(3, 5, PlayerPiece.X),
                Triple(4, 4, PlayerPiece.X),
                Triple(7, 1, PlayerPiece.O),
                Triple(1, 1, PlayerPiece.O)
            ),
            correctMoves = listOf(Pair(1, 7), Pair(5, 3)),
            explanation = "Nối dài đường chéo phụ tại (2, 8) hoặc (6, 4) để tạo thế công 4 quân không thể cản.",
            rewardExp = 60
        ),
        TrainingPuzzle(
            id = 8,
            title = "8. Bẫy Tam Giác Chiến Thuật",
            category = "Chiến thuật",
            description = "Tạo thế cờ tam giác gài bẫy liên hoàn 3 hướng tấn công.",
            boardSize = 10,
            playerPiece = PlayerPiece.X,
            initialPieces = listOf(
                Triple(3, 3, PlayerPiece.X),
                Triple(3, 5, PlayerPiece.X),
                Triple(5, 3, PlayerPiece.X),
                Triple(2, 2, PlayerPiece.O),
                Triple(6, 6, PlayerPiece.O)
            ),
            correctMoves = listOf(Pair(3, 4), Pair(4, 3), Pair(5, 5)),
            explanation = "Điểm kết nối tạo thế cờ gọng kìm 3 hướng mở.",
            rewardExp = 70
        ),
        TrainingPuzzle(
            id = 9,
            title = "9. Phòng Thủ Kết Hợp Phản Công",
            category = "Phản công",
            description = "Vừa chặn chuỗi tấn công của O, vừa mở ra đường 4 quân thắng lợi cho X.",
            boardSize = 10,
            playerPiece = PlayerPiece.X,
            initialPieces = listOf(
                Triple(3, 4, PlayerPiece.O),
                Triple(4, 4, PlayerPiece.O),
                Triple(5, 4, PlayerPiece.O),
                Triple(2, 2, PlayerPiece.X),
                Triple(2, 3, PlayerPiece.X),
                Triple(2, 5, PlayerPiece.X)
            ),
            correctMoves = listOf(Pair(2, 4)),
            explanation = "Ô (3, 5) vừa chặn đứng đầu dọc của O, vừa hoàn thành chuỗi 4 quân ngang cho X!",
            rewardExp = 80
        ),
        TrainingPuzzle(
            id = 10,
            title = "10. Thế Cờ Đại Sư",
            category = "Cao cấp",
            description = "Thế cờ tổng lực phức tạp. Hãy tìm duy nhất 1 nước đi đưa bạn tới chiến thắng bắt buộc trong 2 lượt tới.",
            boardSize = 10,
            playerPiece = PlayerPiece.X,
            initialPieces = listOf(
                Triple(3, 3, PlayerPiece.X),
                Triple(4, 3, PlayerPiece.X),
                Triple(5, 3, PlayerPiece.X),
                Triple(3, 4, PlayerPiece.X),
                Triple(3, 5, PlayerPiece.X),
                Triple(2, 2, PlayerPiece.O),
                Triple(6, 3, PlayerPiece.O),
                Triple(3, 6, PlayerPiece.O)
            ),
            correctMoves = listOf(Pair(4, 4), Pair(2, 3), Pair(3, 2)),
            explanation = "Nước cờ trung tâm chiến lược mở ra cùng lúc 2 đường 4 quân.",
            rewardExp = 100
        )
    )
}
