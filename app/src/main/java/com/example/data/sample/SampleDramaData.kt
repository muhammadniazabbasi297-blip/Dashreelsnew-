package com.example.data.sample

import com.example.R
import com.example.data.model.DramaSeries
import com.example.data.model.Episode

object SampleDramaData {

    val sampleDramas: List<DramaSeries> = listOf(
        DramaSeries(
            id = "billionaire_bride",
            title = "The Billionaire's Secret Bride",
            synopsis = "Forced into an arranged marriage with the reclusive CEO Lucas Vance, Elena thought her life was ruined. But beneath his cold boardroom demeanor lies a fierce protector harboring a 10-year-old promise to keep her safe from high society's deadliest conspirators.",
            posterRes = R.drawable.poster_billionaire_bride,
            category = "CEO Romance",
            tags = listOf("Billionaire", "Arranged Marriage", "Secret Identity", "Sweet Revenge"),
            rating = 4.9,
            viewsCountFormatted = "48.2M",
            totalEpisodes = 45,
            isTrending = true,
            isVipFreeUnlocked = true,
            director = "Elena Rostova",
            cast = listOf("Victoria Reed", "Lucas Vance", "Julian Sterling")
        ),
        DramaSeries(
            id = "shadow_dragon",
            title = "Return of the Shadow Dragon",
            synopsis = "Betrayed and cast out by the five grand syndicates of New Olympus, master martial artist Jin returns after seven grueling years in exile. Armed with forbidden shadow techniques, he dismantles the corrupt underworld empire piece by piece.",
            posterRes = R.drawable.poster_shadow_dragon,
            category = "Urban Action",
            tags = listOf("Action", "Martial Arts", "Revenge", "Underworld", "Overpowered"),
            rating = 4.8,
            viewsCountFormatted = "36.7M",
            totalEpisodes = 50,
            isTrending = true,
            isVipFreeUnlocked = true,
            director = "Kenji Takahashi",
            cast = listOf("Jin Kazama", "Sora Ren", "Marcus Drake")
        ),
        DramaSeries(
            id = "reborn_heiress",
            title = "The Reborn Heiress's Revenge",
            synopsis = "Poisoned by her deceptive stepsister and faithless fiancé on the eve of her inheritance, Scarlett awakens five years in the past—the exact morning of her engagement gala. With future knowledge in hand, she turns the tables and claims her family's trillion-dollar throne.",
            posterRes = R.drawable.poster_reborn_heiress,
            category = "Rebirth & Revenge",
            tags = listOf("Time Travel", "Heiress", "Sweet Revenge", "High Society Drama"),
            rating = 4.9,
            viewsCountFormatted = "52.1M",
            totalEpisodes = 38,
            isTrending = true,
            isVipFreeUnlocked = true,
            director = "Camille Laurent",
            cast = listOf("Scarlett Hayes", "Damian Cross", "Vivian Blackwood")
        ),
        DramaSeries(
            id = "undercover_boss",
            title = "Undercover Mafia Boss",
            synopsis = "To unmask a traitor inside his multinational cartel, billionaire boss Leo disguises himself as an unassuming bodyguard assigned to protect feisty investigative journalist Maya. But as sparks fly, keeping his criminal empire secret becomes his toughest mission.",
            posterRes = R.drawable.poster_undercover_boss,
            category = "Thriller Romance",
            tags = listOf("Enemies to Lovers", "Undercover", "Bodyguard", "Mob Romance"),
            rating = 4.7,
            viewsCountFormatted = "29.4M",
            totalEpisodes = 42,
            isTrending = false,
            isVipFreeUnlocked = true,
            director = "Dario Rossi",
            cast = listOf("Leo Valenti", "Maya Lin", "Antonio Moretti")
        )
    )

    // Reliable vertical and mp4 video streams for seamless playback
    private val videoSampleUrls = listOf(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
    )

    fun getEpisodesForDrama(dramaId: String): List<Episode> {
        val drama = sampleDramas.find { it.id == dramaId } ?: sampleDramas.first()
        val count = drama.totalEpisodes
        return (1..count).map { epNum ->
            val videoUrl = videoSampleUrls[(epNum - 1) % videoSampleUrls.size]
            val subtitle = when (epNum % 4) {
                1 -> "Lucas: \"You don't understand the storm that is coming for us.\""
                2 -> "Elena: \"I never asked for your empire, I only wanted the truth!\""
                3 -> "Announcer: \"The board of directors hereby declares total control.\""
                else -> "\"Wait! The confidential documents were forged from the beginning!\""
            }
            Episode(
                id = "${dramaId}_ep_$epNum",
                dramaId = dramaId,
                episodeNumber = epNum,
                title = "Episode $epNum - ${getEpisodeSubTitle(dramaId, epNum)}",
                durationSeconds = 95 + (epNum * 7) % 65,
                videoUrl = videoUrl,
                subtitleText = subtitle,
                isVipFree = true
            )
        }
    }

    private fun getEpisodeSubTitle(dramaId: String, ep: Int): String {
        return when (ep) {
            1 -> "The Fateful Encounter"
            2 -> "Unmasked at the Gala"
            3 -> "Cold Boardroom Confrontation"
            4 -> "A Dangerous Promise"
            5 -> "The Heir Apparent"
            6 -> "Whispers in the Dark"
            7 -> "The Double Cross"
            8 -> "Truth Under Rain"
            9 -> "Sudden Confession"
            10 -> "Shadows in High Society"
            else -> "Climax of Destiny Part $ep"
        }
    }

    val initialComments = listOf(
        "I'm totally hooked! The cliffhanger at the end had me screaming!",
        "Omg thank you for VIP Free access, I binged 15 episodes in one sitting! 😭🔥",
        "The male lead's eyes whenever he looks at her... I CAN'T EVEN.",
        "Best drama of the season! Sound design and acting are peak tier 👑",
        "No ads and 4K quality unlocked? DashReels is the GOAT!",
        "Episode 7 had me rewinding 3 times. What an insane plot twist!",
        "Can't wait for season 2, please tell me there's more episodes coming!",
        "The stepsister is pure evil but the revenge was SO satisfying!!"
    )
}
