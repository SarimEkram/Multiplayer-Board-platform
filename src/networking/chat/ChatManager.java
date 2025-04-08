package networking.chat;

import java.util.*;

/**
 * Manages the in-game chat system, including message storage, filtering, and retrieval.
 */
public class ChatManager {
    private List<ChatMessage> chatHistory;
    private static final int MAX_MESSAGES = 100; // Limit to prevent memory overflow
    private static final Set<String> BADWORD_LIST = new HashSet<>();

    static {
        // Words obtained from https://github.com/LDNOOBW/List-of-Dirty-Naughty-Obscene-and-Otherwise-Bad-Words/blob/master/en
        String[] badWords = {
                "2g1c", "2 girls 1 cup", "acrotomophilia", "alabama hot pocket", "alaskan pipeline", "anal", "anilingus", "anus", "apeshit", "arsehole",
                "ass", "asshole", "assmunch", "auto erotic", "autoerotic", "babeland", "baby batter", "baby juice", "ball gag", "ball gravy",
                "ball kicking", "ball licking", "ball sack", "ball sucking", "bangbros", "bangbus", "bareback", "barely legal", "barenaked", "bastard",
                "bastardo", "bastinado", "bbw", "bdsm", "beaner", "beaners", "beaver cleaver", "beaver lips", "beastiality", "bestiality",
                "big black", "big breasts", "big knockers", "big tits", "bimbos", "birdlock", "bitch", "bitches", "black cock", "blonde action",
                "blonde on blonde action", "blowjob", "blow job", "blow your load", "blue waffle", "blumpkin", "bollocks", "bondage", "boner", "boob",
                "boobs", "booty call", "brown showers", "brunette action", "bukkake", "bulldyke", "bullet vibe", "bullshit", "bung hole", "bunghole",
                "busty", "butt", "buttcheeks", "butthole", "camel toe", "camgirl", "camslut", "camwhore", "carpet muncher", "carpetmuncher",
                "chocolate rosebuds", "cialis", "circlejerk", "cleveland steamer", "clit", "clitoris", "clover clamps", "clusterfuck", "cock", "cocks",
                "coprolagnia", "coprophilia", "cornhole", "coon", "coons", "creampie", "cum", "cumming", "cumshot", "cumshots",
                "cunnilingus", "cunt", "darkie", "date rape", "daterape", "deep throat", "deepthroat", "dendrophilia", "dick", "dildo",
                "dingleberry", "dingleberries", "dirty pillows", "dirty sanchez", "doggie style", "doggiestyle", "doggy style", "doggystyle", "dog style", "dolcett",
                "domination", "dominatrix", "dommes", "donkey punch", "double dong", "double penetration", "dp action", "dry hump", "dvda", "eat my ass",
                "ecchi", "ejaculation", "erotic", "erotism", "escort", "eunuch", "fag", "faggot", "fecal", "felch",
                "fellatio", "feltch", "female squirting", "femdom", "figging", "fingerbang", "fingering", "fisting", "foot fetish", "footjob",
                "frotting", "fuck", "fuck buttons", "fuckin", "fucking", "fucktards", "fudge packer", "fudgepacker", "futanari", "gangbang",
                "gang bang", "gay sex", "genitals", "giant cock", "girl on", "girl on top", "girls gone wild", "goatcx", "goatse", "god damn",
                "gokkun", "golden shower", "goodpoop", "goo girl", "goregasm", "grope", "group sex", "g-spot", "guro", "hand job",
                "handjob", "hard core", "hardcore", "hentai", "homoerotic", "honkey", "hooker", "horny", "hot carl", "hot chick",
                "how to kill", "how to murder", "huge fat", "humping", "incest", "intercourse", "jack off", "jail bait", "jailbait", "jelly donut",
                "jerk off", "jigaboo", "jiggaboo", "jiggerboo", "jizz", "juggs", "kike", "kinbaku", "kinkster", "kinky",
                "knobbing", "leather restraint", "leather straight jacket", "lemon party", "livesex", "lolita", "lovemaking", "make me come", "male squirting", "masturbate",
                "masturbating", "masturbation", "menage a trois", "milf", "missionary position", "mong", "motherfucker", "mound of venus", "mr hands", "muff diver",
                "muffdiving", "nambla", "nawashi", "negro", "neonazi", "nigga", "nigger", "nig nog", "nimphomania", "nipple",
                "nipples", "nsfw", "nsfw images", "nude", "nudity", "nutten", "nympho", "nymphomania", "octopussy", "omorashi",
                "one cup two girls", "one guy one jar", "orgasm", "orgy", "paedophile", "paki", "panties", "panty", "pedobear", "pedophile",
                "pegging", "penis", "phone sex", "piece of shit", "pikey", "pissing", "piss pig", "pisspig", "playboy", "pleasure chest",
                "pole smoker", "ponyplay", "poof", "poon", "poontang", "punany", "poop chute", "poopchute", "porn", "porno",
                "pornography", "prince albert piercing", "pthc", "pubes", "pussy", "queaf", "queef", "quim", "raghead", "raging boner",
                "rape", "raping", "rapist", "rectum", "reverse cowgirl", "rimjob", "rimming", "rosy palm", "rosy palm and her 5 sisters", "rusty trombone",
                "sadism", "santorum", "scat", "schlong", "scissoring", "semen", "sex", "sexcam", "sexo", "sexy",
                "sexual", "sexually", "sexuality", "shaved beaver", "shaved pussy", "shemale", "shibari", "shit", "shitblimp", "shitty",
                "shota", "shrimping", "skeet", "slanteye", "slut", "s&m", "smut", "snatch", "snowballing", "sodomize",
                "sodomy", "spastic", "spic", "splooge", "splooge moose", "spooge", "spread legs", "spunk", "strap on", "strapon",
                "strappado", "strip club", "style doggy", "suck", "sucks", "suicide girls", "sultry women", "swastika", "swinger", "tainted love",
                "taste my", "tea bagging", "threesome", "throating", "thumbzilla", "tied up", "tight white", "tit", "tits", "titties",
                "titty", "tongue in a", "topless", "tosser", "towelhead", "tranny", "tribadism", "tub girl", "tubgirl", "tushy",
                "twat", "twink", "twinkie", "two girls one cup", "undressing", "upskirt", "urethra play", "urophilia", "vagina", "venus mound",
                "viagra", "vibrator", "violet wand", "vorarephilia", "voyeur", "voyeurweb", "voyuer", "vulva", "wank", "wetback",
                "wet dream", "white power", "whore", "worldsex", "wrapping men", "wrinkled starfish", "xx", "xxx", "yaoi", "yellow showers",
                "yiffy", "zoophilia", "🖕"
        };
        BADWORD_LIST.addAll(Arrays.asList(badWords));
    }

    /**
     * Initializes a new ChatManager with an empty chat history.
     */
    public ChatManager() {
        this.chatHistory = new ArrayList<>();
    }

    /**
     * Adds a new message to the chat history with bad word filtering.
     *
     * @param playerId The player who sent the message.
     * @param message  The content of the message.
     */
    public void addMessage(String playerId, String message) {
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Message cannot be empty!");
            return;
        }

        // Check for bad words
        if (containsBadword(message)) {
            System.out.println("Warning: Message contains inappropriate content!");
            return;
        }

        // If message history exceeds max limit, remove the oldest message
        if (chatHistory.size() >= MAX_MESSAGES) {
            chatHistory.remove(0);
        }

        ChatMessage chatMessage = new ChatMessage(playerId, message);
        chatHistory.add(chatMessage);
    }

    /**
     * Checks if a message contains bad words.
     *
     * @param message The message to check.
     * @return true if bad words are found, false otherwise.
     */
    private boolean containsBadword(String message) {
        String[] words = message.toLowerCase().split("\\s+"); // Split message into words
        for (String word : words) {
            if (BADWORD_LIST.contains(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the entire chat history.
     *
     * @return A list of ChatMessage objects representing chat history.
     */
    public List<ChatMessage> getChatHistory() {
        return new ArrayList<>(chatHistory); // Return a copy to prevent external modification
    }

    /**
     * Clears all messages in the chat history.
     */
    public void clearChatHistory() {
        chatHistory.clear();
        System.out.println("Chat history has been cleared.");
    }
}
