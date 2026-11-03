// Go Mastery for Android - generated from Go_course_v3.py.
// Upload this file unchanged to the root of your GitHub repository.

package com.gomastery.app

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gomastery.app.ui.AboutScreen
import com.gomastery.app.ui.C
import com.gomastery.app.ui.DiagramsScreen
import com.gomastery.app.ui.DrillScreen
import com.gomastery.app.ui.GoTheme
import com.gomastery.app.ui.HomeScreen
import com.gomastery.app.ui.LessonScreen
import com.gomastery.app.ui.PlaygroundScreen
import com.gomastery.app.ui.ReportScreen
import com.gomastery.app.ui.ReviewScreen
import com.gomastery.app.ui.StatsScreen
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

// ===== Course.kt =====
// ---------------------------------------------------------------------------
// Course data, loaded from assets/course.json. That file is extracted verbatim
// from Go_course_v3.py (MODULES, LESSONS, memory diagrams, the drill item bank,
// lint rules and every UI table), so the app teaches exactly the same content.
// ---------------------------------------------------------------------------

data class Quiz(
    val q: String,
    val options: List<String>,
    val answer: Int,
    val explain: String,
    val hint: String?,
)

data class Lesson(
    val index: Int,
    val module: Int,
    val title: String,
    val concept: String,
    val code: String,
    val expected: String?,
    val quiz: Quiz,
    val stdin: String,
    val flags: List<String>,
    val files: Map<String, String>,
    val demo: Boolean,
    val movie: List<String>,
    val video: String?,
    val branchParent: String?,
    val branchChildren: List<String>,
    val anatomy: Boolean,
    val boss: Boolean,
    val isBranch: Boolean,
    val depth: Int,
) {
    val hasCode: Boolean get() = code.isNotBlank()
    /** A write-it-yourself challenge (demos ship working code to just run). */
    val isChallenge: Boolean get() = hasCode && !demo
    val isDemo: Boolean get() = hasCode && demo
    val hasEditor: Boolean get() = hasCode
    val isTest: Boolean get() = "test" in flags
}

data class DiagramRow(
    val name: String, val addr: String, val size: String,
    val value: String, val note: String, val frame: String,
)

data class Diagram(val key: String, val title: String, val rows: List<DiagramRow>)

data class LintRule(
    val sev: String, val pattern: Regex, val why: String, val fix: String, val raw: Boolean,
)

data class Distractor(val text: String, val why: String)

data class DrillItem(
    val id: String,
    val concept: String,
    val module: Int,
    val kind: String,
    val prompt: String?,
    val code: String?,
    val answer: String?,
    val why: String?,
    val diagram: String?,
    val subgoals: String?,
    val rows: List<DiagramRow>,
    val question: String?,
    val blocks: Map<String, String>,
    val distractors: Map<String, Distractor>,
    val solution: List<String>,
    val blanks: List<List<String>>,
)

data class UiTables(
    val walkRules: List<Pair<Regex, String>>,
    val hintRules: List<Pair<Regex, String>>,
    val errorHints: List<Pair<Regex, String>>,
    val ranks: List<Pair<Int, String>>,
    val searchEngine: String,
    val searchPrompt: String,
    val keywords: Set<String>,
    val types: Set<String>,
    val funcs: Set<String>,
    val selfExplain: List<String>,
    val easyPrefixes: List<String>,
)

class Course(
    val modules: List<String>,
    val lessons: List<Lesson>,
    val diagrams: List<Diagram>,
    val subgoals: Map<String, List<String>>,
    val lintRules: List<LintRule>,
    val drillItems: List<DrillItem>,
    val ui: UiTables,
) {
    private val byTitle: Map<String, Lesson> = lessons.associateBy { it.title }
    fun lesson(title: String?): Lesson? = title?.let { byTitle[it] }
    fun diagram(key: String?): Diagram? = diagrams.firstOrNull { it.key == key }

    companion object {
        fun load(ctx: Context): Course {
            val text = ctx.assets.open("course.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
            return parse(JSONObject(text))
        }

        private fun JSONObject.str(k: String): String? = if (isNull(k)) null else optString(k)
        private fun JSONArray?.strings(): List<String> =
            if (this == null) emptyList() else List(length()) { getString(it) }

        private fun JSONObject?.stringMap(): Map<String, String> {
            if (this == null) return emptyMap()
            val m = LinkedHashMap<String, String>()
            val it = keys()
            while (it.hasNext()) { val k = it.next(); m[k] = getString(k) }
            return m
        }

        private fun rows(a: JSONArray?): List<DiagramRow> {
            if (a == null) return emptyList()
            return List(a.length()) { i ->
                val r = a.getJSONObject(i)
                DiagramRow(
                    r.optString("name", ""), r.optString("addr", ""), r.optString("size", ""),
                    r.optString("value", ""), r.optString("note", ""), r.optString("frame", ""),
                )
            }
        }

        private fun rulePairs(a: JSONArray): List<Pair<Regex, String>> =
            List(a.length()) { i ->
                val r = a.getJSONArray(i)
                Regex(r.getString(0)) to r.getString(1)
            }

        fun parse(root: JSONObject): Course {
            val modules = root.getJSONArray("modules").strings()
            val la = root.getJSONArray("lessons")
            val lessons = List(la.length()) { i ->
                val o = la.getJSONObject(i)
                val q = o.getJSONObject("quiz")
                Lesson(
                    index = i,
                    module = o.getInt("module"),
                    title = o.getString("title"),
                    concept = o.getString("concept"),
                    code = o.optString("code", ""),
                    expected = o.str("expected"),
                    quiz = Quiz(
                        q.getString("q"), q.getJSONArray("options").strings(), q.getInt("answer"),
                        q.optString("explain", ""), q.str("hint"),
                    ),
                    stdin = o.optString("stdin", ""),
                    flags = o.optJSONArray("flags").strings(),
                    files = o.optJSONObject("files").stringMap(),
                    demo = o.optBoolean("demo", false),
                    movie = o.optJSONArray("movie").strings(),
                    video = o.str("video"),
                    branchParent = o.str("branch_parent"),
                    branchChildren = o.optJSONArray("branch_children").strings(),
                    anatomy = o.optBoolean("anatomy", false),
                    boss = o.optBoolean("boss", false),
                    isBranch = o.optBoolean("is_branch", false),
                    depth = o.optInt("branch_depth", 0),
                )
            }
            val da = root.getJSONArray("diagrams")
            val diagrams = List(da.length()) { i ->
                val d = da.getJSONObject(i)
                Diagram(d.getString("key"), d.getString("title"), rows(d.getJSONArray("rows")))
            }
            val sg = root.getJSONObject("subgoals")
            val subgoals = LinkedHashMap<String, List<String>>()
            sg.keys().forEach { k -> subgoals[k] = sg.getJSONArray(k).strings() }

            val ra = root.getJSONArray("lintRules")
            val lintRules = List(ra.length()) { i ->
                val r = ra.getJSONObject(i)
                LintRule(r.getString("sev"), Regex(r.getString("pattern")), r.getString("why"),
                    r.getString("fix"), r.optBoolean("raw", false))
            }

            val ia = root.getJSONArray("drillItems")
            val items = List(ia.length()) { i ->
                val o = ia.getJSONObject(i)
                val dis = LinkedHashMap<String, Distractor>()
                o.optJSONObject("distractors")?.let { d ->
                    d.keys().forEach { k ->
                        val x = d.getJSONObject(k)
                        dis[k] = Distractor(x.getString("text"), x.getString("why"))
                    }
                }
                val blanks = o.optJSONArray("blanks")?.let { b -> List(b.length()) { j -> b.getJSONArray(j).strings() } }
                    ?: emptyList()
                DrillItem(
                    id = o.getString("id"), concept = o.getString("concept"), module = o.getInt("module"),
                    kind = o.getString("kind"), prompt = o.str("prompt"), code = o.str("code"),
                    answer = o.str("answer"), why = o.str("why"), diagram = o.str("diagram"),
                    subgoals = o.str("subgoals"), rows = rows(o.optJSONArray("rows")),
                    question = o.str("question"), blocks = o.optJSONObject("blocks").stringMap(),
                    distractors = dis, solution = o.optJSONArray("solution").strings(), blanks = blanks,
                )
            }

            val u = root.getJSONObject("ui")
            val ra2 = u.getJSONArray("ranks")
            val ui = UiTables(
                walkRules = rulePairs(u.getJSONArray("walkRules")),
                hintRules = rulePairs(u.getJSONArray("hintRules")),
                errorHints = rulePairs(u.getJSONArray("errorHints")),
                ranks = List(ra2.length()) { i -> ra2.getJSONArray(i).let { it.getInt(0) to it.getString(1) } },
                searchEngine = u.getString("searchEngine"),
                searchPrompt = u.getString("searchPrompt"),
                keywords = u.getJSONArray("keywords").strings().toSet(),
                types = u.getJSONArray("types").strings().toSet(),
                funcs = u.getJSONArray("funcs").strings().toSet(),
                selfExplain = u.getJSONArray("selfExplain").strings(),
                easyPrefixes = u.getJSONArray("easyPrefixes").strings(),
            )
            return Course(modules, lessons, diagrams, subgoals, lintRules, items, ui)
        }
    }
}

// ===== Store.kt =====
/** Everything the Python app kept in ~/.go_mastery_*.json, kept in SharedPreferences. */
class Store(ctx: Context) {
    private val p = ctx.getSharedPreferences("go_mastery", Context.MODE_PRIVATE)

    fun completed(): MutableSet<String> = HashSet(p.getStringSet("completed", emptySet()) ?: emptySet())
    fun saveCompleted(s: Set<String>) = p.edit().putStringSet("completed", HashSet(s)).apply()

    fun obj(key: String): JSONObject = try {
        JSONObject(p.getString(key, "{}") ?: "{}")
    } catch (e: Exception) {
        JSONObject()
    }

    fun saveObj(key: String, o: JSONObject) = p.edit().putString(key, o.toString()).apply()

    fun int(key: String, def: Int) = p.getInt(key, def)
    fun saveInt(key: String, v: Int) = p.edit().putInt(key, v).apply()
    fun bool(key: String, def: Boolean) = p.getBoolean(key, def)
    fun saveBool(key: String, v: Boolean) = p.edit().putBoolean(key, v).apply()
}

/**
 * Port of the Python _Engage engine: streaks with freezes, XP and ranks, a
 * daily goal, lucky gems and a fresh-start message after a break.
 */
class Engage(private val store: Store, private val ranks: List<Pair<Int, String>>) {
    private val d: JSONObject = store.obj("engage").also { o ->
        val defaults = mapOf<String, Any>(
            "xp" to 0, "streak" to 0, "best_streak" to 0, "freezes" to 0,
            "today_count" to 0, "goal" to 5, "gems" to 0,
        )
        defaults.forEach { (k, v) -> if (!o.has(k)) o.put(k, v) }
        if (!o.has("history")) o.put("history", JSONObject())
        if (!o.has("rewarded")) o.put("rewarded", JSONArray())
    }

    private fun today(): LocalDate = LocalDate.now()
    private fun save() = store.saveObj("engage", d)
    private fun s(k: String): String? = if (d.isNull(k)) null else d.optString(k)

    val xp get() = d.optInt("xp")
    val goal get() = d.optInt("goal", 5)
    val gems get() = d.optInt("gems")
    val bestStreak get() = d.optInt("best_streak")
    val freezes get() = d.optInt("freezes")
    val todayCount: Int get() = if (s("day") == today().toString()) d.optInt("today_count") else 0

    fun setGoal(n: Int) { d.put("goal", n.coerceIn(1, 50)); save() }

    fun rank(xp: Int): Pair<String, Pair<Int, String>?> {
        var name = ranks.first().second
        var next: Pair<Int, String>? = null
        ranks.forEachIndexed { i, (t, n) ->
            if (xp >= t) {
                name = n
                next = if (i + 1 < ranks.size) ranks[i + 1] else null
            }
        }
        return name to next
    }

    fun streakAlive(): Int {
        val last = s("last_day") ?: return 0
        val gap = try {
            (today().toEpochDay() - LocalDate.parse(last).toEpochDay()).toInt()
        } catch (e: Exception) { return 0 }
        return if (gap <= 1 || gap - 1 <= d.optInt("freezes")) d.optInt("streak") else 0
    }

    private fun rewarded(): JSONArray = d.optJSONArray("rewarded") ?: JSONArray().also { d.put("rewarded", it) }
    fun wasRewarded(title: String): Boolean {
        val a = rewarded()
        for (i in 0 until a.length()) if (a.optString(i) == title) return true
        return false
    }

    /** Call when a lesson is newly completed. Each title is rewarded once. */
    fun record(boss: Boolean, title: String?): Pair<Int, List<String>> {
        if (title != null) {
            if (wasRewarded(title)) return 0 to emptyList()
            rewarded().put(title)
        }
        val iso = today().toString()
        val msgs = mutableListOf<String>()
        if (s("last_day") != iso) {
            val last = s("last_day")
            if (last == null) {
                d.put("streak", 1)
                msgs += "Day 1 of your streak! Finish one lesson tomorrow to make it 2."
            } else {
                val gap = (today().toEpochDay() - LocalDate.parse(last).toEpochDay()).toInt()
                if (gap == 1) {
                    d.put("streak", d.optInt("streak") + 1)
                } else if (gap > 1) {
                    val missed = gap - 1
                    if (missed <= d.optInt("freezes")) {
                        d.put("freezes", d.optInt("freezes") - missed)
                        d.put("streak", d.optInt("streak") + 1)
                        msgs += "Streak freeze used for $missed missed day(s): your ${d.optInt("streak")}-day streak is safe."
                    } else {
                        if (d.optInt("streak") >= 3) {
                            msgs += "Welcome back! Fresh start: day 1 of a new streak (your best is ${d.optInt("best_streak")})."
                        }
                        d.put("streak", 1)
                    }
                }
            }
            val st = d.optInt("streak")
            if (st > 0 && st % 7 == 0 && d.optInt("freezes") < 2) {
                d.put("freezes", d.optInt("freezes") + 1)
                msgs += "You earned a streak freeze: it will protect one missed day."
            }
            if (st in listOf(3, 7, 14, 21, 30, 50, 75, 100, 150, 200, 365)) {
                msgs += "$st-day streak! That's the kind of consistency that builds real skill."
            }
            d.put("best_streak", maxOf(d.optInt("best_streak"), st))
            d.put("last_day", iso)
        }
        if (s("day") != iso) {
            d.put("day", iso)
            d.put("today_count", 0)
        }
        val oldRank = rank(xp).first
        var gain = if (boss) 25 else 10
        if (Random.nextDouble() < 1.0 / 7) {
            val bonus = listOf(5, 10, 15, 20).random()
            gain += bonus
            d.put("gems", d.optInt("gems") + 1)
            msgs += "Lucky gem! +$bonus bonus XP."
        }
        d.put("xp", xp + gain)
        d.put("today_count", d.optInt("today_count") + 1)
        val h = d.optJSONObject("history") ?: JSONObject().also { d.put("history", it) }
        h.put(iso, h.optInt(iso) + 1)
        if (d.optInt("today_count") == goal) {
            msgs += "Daily goal reached ($goal lessons)! Anything more today is a bonus."
        }
        val newRank = rank(xp).first
        if (newRank != oldRank) msgs += "RANK UP: you are now a $newRank!"
        save()
        return gain to msgs
    }

    fun summary(): String {
        val (name, next) = rank(xp)
        var s = "♨ ${streakAlive()}-day streak · ★ $xp XP · $name · today $todayCount/$goal"
        if (next != null) s += " · ${next.first - xp} XP to ${next.second}"
        if (freezes > 0) s += " · $freezes freeze${if (freezes == 1) "" else "s"}"
        return s
    }
}

// ===== GoToolchain.kt =====
data class RunResult(val status: String, val text: String, val note: String? = null)

/**
 * The real Go toolchain, running on the phone.
 *
 * The APK carries the complete official Go distribution (goroot.zip) with
 * every tool rebuilt for Android arm64, plus a pre-built build cache
 * (gocache.zip). On first launch both are unpacked into the app's private
 * storage; after that `go build`, `go test` and `go vet` run fully offline.
 */
class GoToolchain(private val ctx: Context, private val errorHints: List<Pair<Regex, String>>) {
    private val base = File(ctx.filesDir, "go")
    val goroot = File(base, "goroot")
    private val cache = File(base, "gocache")
    private val home = File(base, "home")
    private val tmp = File(base, "tmp")
    private val work = File(base, "work")
    private val marker = File(base, "installed.txt")

    private val bundleInfo: Map<String, String> by lazy {
        try {
            ctx.assets.open("go-bundle.txt").bufferedReader().readLines()
                .mapNotNull { l -> l.split("=", limit = 2).takeIf { it.size == 2 }?.let { it[0] to it[1] } }
                .toMap()
        } catch (e: IOException) {
            emptyMap()
        }
    }

    val bundled: Boolean get() = bundleInfo.isNotEmpty()
    val abiSupported: Boolean get() = Build.SUPPORTED_ABIS.any { it == "arm64-v8a" }
    val bundleId: String get() = bundleInfo.entries.sortedBy { it.key }.joinToString(";") { "${it.key}=${it.value}" }

    fun installed(): Boolean =
        bundled && marker.exists() && marker.readText() == bundleId && File(goroot, "bin/go").canExecute()

    val versionText: String
        get() = try {
            File(goroot, "VERSION").readLines().first().trim()
        } catch (e: Exception) {
            bundleInfo["version"] ?: "go"
        }

    /** (major, minor) of the bundled Go, e.g. 1 to 26. */
    fun version(): Pair<Int, Int>? =
        Regex("""go(\d+)\.(\d+)""").find(versionText)?.let {
            it.groupValues[1].toInt() to it.groupValues[2].toInt()
        }

    /**
     * Unpacks the toolchain. Reports progress 0..1. Safe to call again after
     * an app update: it only re-extracts when the bundle changed.
     */
    fun install(progress: (String, Float) -> Unit) {
        if (installed()) return
        if (!bundled) throw IOException("This APK was built without the Go toolchain bundle.")
        base.mkdirs()
        marker.delete()
        progress("Removing the previous toolchain", 0f)
        goroot.deleteRecursively()
        cache.deleteRecursively()
        val nGo = bundleInfo["goroot_entries"]?.toIntOrNull() ?: 30000
        val nCache = bundleInfo["cache_entries"]?.toIntOrNull() ?: 5000
        val total = (nGo + nCache).coerceAtLeast(1).toFloat()
        var done = 0
        unzip(ctx.assets.open("goroot.zip"), base) { name ->
            done++
            if (done % 200 == 0) progress("Unpacking Go: $name", done / total)
        }
        cache.mkdirs()
        unzip(ctx.assets.open("gocache.zip"), cache) {
            done++
            if (done % 200 == 0) progress("Unpacking the pre-built standard library", done / total)
        }
        progress("Making the tools executable", 0.99f)
        File(goroot, "bin").listFiles()?.forEach { it.setExecutable(true, false) }
        File(goroot, "pkg/tool").listFiles()?.forEach { dir ->
            dir.listFiles()?.forEach { it.setExecutable(true, false) }
        }
        // shell scripts shipped with Go (all.bash, make.bash ...) keep working too
        goroot.walkTopDown().filter { it.isFile && (it.name.endsWith(".bash") || it.name.endsWith(".sh")) }
            .forEach { it.setExecutable(true, false) }
        listOf(home, tmp, work).forEach { it.mkdirs() }
        marker.writeText(bundleId)
        progress("Ready", 1f)
    }

    private fun unzip(input: InputStream, dest: File, onEntry: (String) -> Unit) {
        val destPath = dest.canonicalPath + File.separator
        ZipInputStream(input.buffered(1 shl 16)).use { zin ->
            val buf = ByteArray(1 shl 16)
            while (true) {
                val e = zin.nextEntry ?: break
                val out = File(dest, e.name)
                if (!out.canonicalPath.startsWith(destPath)) throw IOException("bad zip entry ${e.name}")
                if (e.isDirectory) {
                    out.mkdirs()
                } else {
                    out.parentFile?.mkdirs()
                    out.outputStream().buffered(1 shl 16).use { o ->
                        while (true) {
                            val n = zin.read(buf)
                            if (n < 0) break
                            o.write(buf, 0, n)
                        }
                    }
                }
                onEntry(e.name)
            }
        }
    }

    fun env(): Map<String, String> = mapOf(
        "GOROOT" to goroot.path,
        "GOCACHE" to cache.path,
        "GOPATH" to File(home, "go").path,
        "GOMODCACHE" to File(home, "go/pkg/mod").path,
        "HOME" to home.path,
        "USER" to "gopher",
        "TMPDIR" to tmp.path,
        "GOTMPDIR" to tmp.path,
        "PATH" to "${goroot.path}/bin:/system/bin:/system/xbin:/vendor/bin",
        "GOTOOLCHAIN" to "local",      // never try to download anything
        "GOPROXY" to "off",
        "GOSUMDB" to "off",
        "GOFLAGS" to "-mod=mod",
        "GOTELEMETRY" to "off",
        "GO111MODULE" to "on",
        "CGO_ENABLED" to "0",           // there is no C compiler on a phone
    )

    data class ProcOut(val code: Int, val out: String, val err: String, val timedOut: Boolean)

    private val outLimit = 400_000

    fun exec(cmd: List<String>, dir: File, stdin: String?, timeoutMs: Long): ProcOut {
        val pb = ProcessBuilder(cmd).directory(dir)
        pb.environment().putAll(env())
        val p = pb.start()
        val out = StringBuilder()
        val err = StringBuilder()
        fun pump(s: InputStream, sb: StringBuilder) = thread(isDaemon = true) {
            try {
                s.bufferedReader().use { r ->
                    val cb = CharArray(8192)
                    while (true) {
                        val n = r.read(cb)
                        if (n < 0) break
                        synchronized(sb) { if (sb.length < outLimit) sb.append(cb, 0, n) }
                    }
                }
            } catch (_: IOException) {
            }
        }
        val t1 = pump(p.inputStream, out)
        val t2 = pump(p.errorStream, err)
        try {
            p.outputStream.use { if (!stdin.isNullOrEmpty()) it.write(stdin.toByteArray()) }
        } catch (_: IOException) {
        }
        val finished = p.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
        if (!finished) p.destroyForcibly()
        t1.join(3000)
        t2.join(3000)
        val o = synchronized(out) { out.toString() }
        val e = synchronized(err) { err.toString() }
        return ProcOut(if (finished) p.exitValue() else -1, o, e, !finished)
    }

    fun explainError(stderr: String): String? =
        errorHints.firstOrNull { it.first.containsMatchIn(stderr) }?.second

    private val testTiming = Regex(""" \(\d+\.\d+s\)""")
    private val testOkLine = Regex("""^(ok|FAIL)\s+(\S+)\s+[\d.]+s$""", RegexOption.MULTILINE)
    private fun normTest(s: String): String =
        testOkLine.replace(testTiming.replace(s, "")) { m -> "${m.groupValues[1]}  ${m.groupValues[2]}" }

    private fun minGo(flags: List<String>): Pair<Int, Int>? =
        flags.firstNotNullOfOrNull { f ->
            Regex("""^go(\d+)\.(\d+)$""").find(f)?.let { it.groupValues[1].toInt() to it.groupValues[2].toInt() }
        }

    private fun newWorkDir(): File {
        work.mkdirs()
        // keep only the last few runs around
        work.listFiles()?.sortedBy { it.lastModified() }?.dropLast(4)?.forEach { it.deleteRecursively() }
        val d = File(work, "run-" + System.nanoTime())
        d.mkdirs()
        return d
    }

    private fun goModLine(): String {
        val v = version() ?: (1 to 22)
        return "module lesson\n\ngo ${v.first}.${v.second}\n"
    }

    fun notReadyReason(): String? = when {
        !abiSupported -> "This phone's processor (${Build.SUPPORTED_ABIS.joinToString()}) isn't arm64, " +
            "so the bundled Go compiler can't run on it. Lessons, quizzes and the recorded outputs still work."
        !bundled -> "This build of the app doesn't include the Go toolchain bundle."
        !installed() -> "Go is still being unpacked. It only happens once - try again in a moment."
        else -> null
    }

    /**
     * Same contract as the Python run_go_code: status is one of ok,
     * compile_error, test_fail, timeout, no_go, needs_newer_go, needs_cgo, error.
     */
    fun run(code: String, stdin: String, flags: List<String>, files: Map<String, String>, expected: String?): RunResult {
        notReadyReason()?.let { return RunResult("no_go", it) }
        if ("cgo" in flags) {
            return RunResult(
                "needs_cgo",
                "This lesson uses cgo, which needs a C compiler (gcc or clang) next to Go. Phones don't " +
                    "have one, so the Go compiler here runs with CGO_ENABLED=0." +
                    (expected?.let { "\n\nRecorded output:\n$it" } ?: ""),
            )
        }
        val need = minGo(flags)
        val have = version()
        if (need != null && have != null && compareValuesBy(have, need, { it.first }, { it.second }) < 0) {
            return RunResult(
                "needs_newer_go",
                "This lesson uses a feature added in Go ${need.first}.${need.second}; the bundled " +
                    "toolchain is $versionText." + (expected?.let { "\n\nRecorded output:\n$it" } ?: ""),
            )
        }
        val note = if ("race" in flags) {
            "The race detector needs cgo, which isn't available on a phone, so this ran without -race."
        } else null
        val dir = newWorkDir()
        return try {
            File(dir, if ("test" in flags) "lesson_test.go" else "main.go").writeText(code)
            File(dir, "go.mod").writeText(goModLine())
            files.forEach { (name, content) ->
                val f = File(dir, name)
                f.parentFile?.mkdirs()
                f.writeText(content)
            }
            val go = File(goroot, "bin/go").path
            if ("test" in flags) {
                val cmd = mutableListOf(go, "test", "-v", "-count=1")
                if ("bench" in flags) cmd += listOf("-run=^$", "-bench=.", "-benchtime=100x")
                cmd += "."
                val r = exec(cmd, dir, stdin, 15 * 60_000L)
                if (r.timedOut) return RunResult("timeout", "The tests took too long and were stopped.", note)
                val out = normTest((r.out + r.err).replace(dir.path, "."))
                if (r.code != 0) {
                    if ("[build failed]" in out || "[setup failed]" in out) {
                        val tip = explainError(out)
                        return RunResult("compile_error", (tip?.let { "HINT: $it\n\n" } ?: "") + out, note)
                    }
                    return RunResult("test_fail", out, note)
                }
                return RunResult("ok", out, note)
            }
            val b = exec(listOf(go, "build", "-o", "prog", "."), dir, null, 15 * 60_000L)
            if (b.timedOut) return RunResult("timeout", "Compilation took too long and was stopped.", note)
            if (b.code != 0) {
                val err = (b.err.ifBlank { b.out }.ifBlank { "Unknown compile error." })
                    .replace(dir.path, ".").replace("# lesson\n", "")
                val tip = explainError(err)
                return RunResult("compile_error", (tip?.let { "HINT: $it\n\n" } ?: "") + err, note)
            }
            val prog = File(dir, "prog")
            prog.setExecutable(true, false)
            val r = exec(listOf(prog.path), dir, stdin, 20_000L)
            if (r.timedOut) {
                return RunResult(
                    "timeout",
                    "Program ran too long and was stopped (an infinite loop, or a goroutine deadlock?)." +
                        (if (r.out.isNotEmpty()) "\n\nOutput so far:\n${r.out}" else ""),
                    note,
                )
            }
            var out = r.out
            if (r.err.isNotEmpty()) out += "\n[stderr]\n" + r.err
            if (r.code != 0) out += "\n[exited with code ${r.code}]"
            RunResult("ok", out.ifEmpty { "(program produced no output)" }, note)
        } catch (e: Exception) {
            RunResult("error", "Unexpected error: ${e.message ?: e.javaClass.simpleName}", note)
        }
    }

    /** go vet on a single main.go, for the Check tab. */
    fun vet(code: String): String? {
        if (notReadyReason() != null) return null
        val dir = newWorkDir()
        File(dir, "main.go").writeText(code)
        File(dir, "go.mod").writeText(goModLine())
        val r = exec(listOf(File(goroot, "bin/go").path, "vet", "."), dir, null, 10 * 60_000L)
        if (r.timedOut) return "go vet took too long and was stopped."
        return (r.err + r.out).replace(dir.path, ".").replace("# lesson\n", "").trim()
    }

    /** Arbitrary go command in a scratch module: powers the "Go terminal" screen. */
    fun goCommand(args: List<String>, code: String, stdin: String): ProcOut {
        val dir = newWorkDir()
        File(dir, "main.go").writeText(code)
        File(dir, "go.mod").writeText(goModLine())
        return exec(listOf(File(goroot, "bin/go").path) + args, dir, stdin, 15 * 60_000L)
            .let { it.copy(out = it.out.replace(dir.path, "."), err = it.err.replace(dir.path, ".")) }
    }
}

// ===== Teaching.kt =====
// Ports of the Python helpers: code_walkthrough, make_hint, make_starter,
// make_objective, challenge_check_mode, the lint sweep, the typo check and
// render_memory. The rule tables come from course.json (extracted verbatim).

class Teaching(private val course: Course) {
    private val ui = course.ui

    fun difficulty(l: Lesson): String {
        if (l.anatomy) return "easy"
        val name = course.modules[l.module]
        return if (ui.easyPrefixes.any { name.startsWith(it) }) "easy" else "hard"
    }

    fun walkthrough(code: String): String {
        val out = mutableListOf<String>()
        for (raw in code.split("\n")) {
            val stripped = raw.trimEnd().trim()
            if (stripped.isEmpty()) continue
            val why = ui.walkRules.firstOrNull { it.first.containsMatchIn(stripped) }?.second ?: "runs this statement"
            out += "$stripped\n     -> $why"
        }
        return out.joinToString("\n")
    }

    fun hint(l: Lesson): String? {
        if (difficulty(l) == "easy") return null
        val found = mutableListOf<String>()
        for ((re, txt) in ui.hintRules) {
            if (re.containsMatchIn(l.code) && txt !in found) found += txt
            if (found.size >= 4) break
        }
        return if (found.isEmpty()) null else "You'll probably want: " + found.joinToString("; ") + "."
    }

    fun objective(l: Lesson): String? {
        if (!l.isChallenge) return null
        val exp = l.expected
        return if (exp != null && exp.isNotBlank()) {
            "Write a program whose output is EXACTLY this:\n\n" + exp.trimEnd('\n') +
                "\n\n(Use the stdin box if the task needs typed input.)"
        } else {
            "Build the program described in the notes below. Goal: it should BUILD and RUN cleanly. " +
                "The exact output can vary, so this one isn't auto-matched - compare yours to the " +
                "reference if shown."
        }
    }

    /** demo = just run it; strict = output must match; runs = must build and run. */
    fun checkMode(l: Lesson): String = when {
        l.demo -> "demo"
        l.expected != null -> "strict"
        else -> "runs"
    }

    /** Keeps the package clause, imports (and test shape); leaves the logic to the learner. */
    fun starter(l: Lesson): String {
        val lines = l.code.split("\n")
        val imports = mutableListOf<String>()
        var pkg = "package main"
        var i = 0
        while (i < lines.size) {
            val s = lines[i].trim()
            when {
                s.startsWith("package ") -> pkg = s
                s.startsWith("import (") -> {
                    val block = mutableListOf(lines[i])
                    i++
                    while (i < lines.size && lines[i].trim() != ")") { block += lines[i]; i++ }
                    block += ")"
                    imports += block.joinToString("\n")
                }
                s.startsWith("import ") -> imports += lines[i]
            }
            i++
        }
        var head = "$pkg\n\n"
        if (imports.isNotEmpty()) {
            head += "// These are the imports the reference solution uses. Go refuses\n" +
                "// to compile an unused import, so delete any you don't need.\n" +
                imports.joinToString("\n") + "\n\n"
        }
        return if (l.isTest) {
            head + "func TestTask(t *testing.T) {\n\t// TODO: write your test(s) here.\n}\n"
        } else {
            head + "func main() {\n\t// TODO: write your solution here.\n" +
                "\t// Read YOUR TASK above; press 'Show solution' if stuck.\n}\n"
        }
    }

    fun conceptText(l: Lesson): String {
        val parts = mutableListOf<String>()
        val obj = objective(l)
        if (obj != null) {
            parts += "▸  YOUR TASK\n$obj"
            hint(l)?.let { parts += "▸  HINT\n$it" }
            parts += "▸  HOW IT WORKS\n${l.concept}"
        } else {
            parts += l.concept
        }
        val walk = walkthrough(l.code)
        if (walk.isNotEmpty()) parts += "▸  LINE BY LINE\n$walk"
        return parts.joinToString("\n\n")
    }

    fun breadcrumb(l: Lesson): String? {
        if (l.branchParent == null && l.branchChildren.isEmpty()) return null
        val chain = mutableListOf<String>()
        val seen = HashSet<String>()
        var cur: Lesson? = l
        while (cur != null && seen.add(cur.title)) {
            chain += cur.title.trimStart('⚛', '✦', '☢', '◉', '★', ' ').trim()
            cur = course.lesson(cur.branchParent)
        }
        return chain.reversed().joinToString(" › ")
    }

    /** Lines for a Parsons puzzle: the solution minus blank lines (3..22 lines). */
    fun parsonsLines(l: Lesson): List<String>? {
        if (!l.hasCode || l.expected == null) return null
        val lines = l.code.split("\n").filter { it.isNotBlank() }.map { it.replace("\t", "    ") }
        return if (lines.size in 3..22) lines else null
    }

    // ------------------------------------------------------------ typo check
    private val known: Set<String> by lazy {
        ui.keywords + ui.types + ui.funcs + setOf(
            "nil", "true", "false", "iota", "fmt", "os", "strings", "strconv", "errors", "sync", "time",
            "context", "slices", "maps", "sort", "bufio", "io", "math", "http", "json", "testing", "func",
            "err", "ok", "ctx",
        )
    }

    private fun editDistLe1(a0: String, b0: String): Boolean {
        if (a0 == b0) return true
        var a = a0
        var b = b0
        if (kotlin.math.abs(a.length - b.length) > 1) return false
        if (a.length == b.length) {
            val diffs = a.indices.filter { a[it] != b[it] }
            if (diffs.size == 1) return true
            if (diffs.size == 2 && diffs[1] == diffs[0] + 1) {
                val (i, j) = diffs
                return a[i] == b[j] && a[j] == b[i]
            }
            return false
        }
        if (a.length > b.length) { val t = a; a = b; b = t }
        var i = 0
        var j = 0
        var skipped = false
        while (i < a.length && j < b.length) {
            if (a[i] != b[j]) {
                if (skipped) return false
                skipped = true
                j++
            } else { i++; j++ }
        }
        return true
    }

    fun typoHints(code: String): String {
        val clean = Regex("""//[^\n]*|/\*[\s\S]*?\*/|"(\\.|[^"\\])*"|`[^`]*`""").replace(code, " ")
        val seen = HashSet<String>()
        val sugg = mutableListOf<Pair<String, String>>()
        for (m in Regex("""[A-Za-z_]{2,}""").findAll(clean)) {
            val tok = m.value
            if (tok in known || !seen.add(tok)) continue
            val kw = known.firstOrNull { it != tok && kotlin.math.abs(it.length - tok.length) <= 1 && editDistLe1(tok, it) }
            if (kw != null) sugg += tok to kw
            if (sugg.size >= 3) break
        }
        if (sugg.isEmpty()) return ""
        return "Possible typo:  " + sugg.joinToString("  ") { "\"${it.first}\" - did you mean \"${it.second}\"?" }
    }

    // ------------------------------------------------------- lint sweep
    data class Finding(val line: Int, val sev: String, val src: String, val why: String, val fix: String, val subgoals: String?)

    private val sevOrder = mapOf("BLOCKING" to 0, "MAJOR" to 1, "MINOR" to 2, "POLISH" to 3)

    private fun stripNoise(line: String): String {
        var s = Regex("\"(?:\\\\.|[^\"\\\\])*\"").replace(line, "\"\"")
        s = Regex("""'(?:\\.|[^'\\])*'""").replace(s, "''")
        s = Regex("""`[^`]*`""").replace(s, "``")
        s = Regex("""//.*$""").replace(s, "")
        return s
    }

    fun lint(text: String): List<Finding> {
        val lines = text.split("\n")
        val hits = mutableListOf<Finding>()
        lines.forEachIndexed { i, raw ->
            val stripped = stripNoise(raw)
            for (r in course.lintRules) {
                if (r.pattern.containsMatchIn(if (r.raw) raw else stripped)) {
                    hits += Finding(i + 1, r.sev, raw.trim(), r.why, r.fix, null)
                }
            }
        }
        // wg.Add inside the goroutine it counts
        lines.forEachIndexed { i, raw ->
            if (Regex("""\bgo\s+func\s*\(""").containsMatchIn(stripNoise(raw))) {
                for (j in i + 1 until minOf(i + 3, lines.size)) {
                    val m = Regex("""\b(\w+)\.Add\s*\(""").find(stripNoise(lines[j]))
                    if (m != null) {
                        val v = m.groupValues[1]
                        hits += Finding(j + 1, "BLOCKING", lines[j].trim(),
                            "$v.Add runs inside the goroutine it is counting. Wait() can return before it ever runs.",
                            "$v.Add(1) on the line BEFORE 'go func'", "goroutines")
                        break
                    }
                }
            }
        }
        // defer inside a loop
        var depth = 0
        val loopDepths = ArrayDeque<Int>()
        lines.forEachIndexed { i, raw ->
            val s = stripNoise(raw)
            val delta = s.count { it == '{' } - s.count { it == '}' }
            if (Regex("""^\s*for\b""").containsMatchIn(s)) loopDepths.addLast(depth + delta)
            if (loopDepths.isNotEmpty() && Regex("""^\s*defer\b""").containsMatchIn(s) &&
                !Regex("""\bfunc\s*\(""").containsMatchIn(s)
            ) {
                hits += Finding(i + 1, "MAJOR", raw.trim(),
                    "defer inside a loop runs when the FUNCTION returns, not at the end of each iteration - every " +
                        "open file or held lock piles up until then.",
                    "move the loop body into a helper func and defer there", "resource")
            }
            depth += delta
            while (loopDepths.isNotEmpty() && depth < loopDepths.last()) loopDepths.removeLast()
        }
        // single-result type assertion
        lines.forEachIndexed { i, raw ->
            val m = Regex("""^\s*([\w.]+)\s*:?=\s*[\w.\]\[]+\.\((?!type\))([\w.*\[\]]+)\)\s*$""").find(stripNoise(raw))
            if (m != null) {
                hits += Finding(i + 1, "MINOR", raw.trim(),
                    "A single-result type assertion PANICS if the value holds a different type.",
                    "${m.groupValues[1]}, ok := v.(${m.groupValues[2]}); if !ok { ... }", null)
            }
        }
        // math/rand near secrets
        if ("\"math/rand" in text &&
            Regex("""(?i)\b(token|password|passwd|secret|nonce|salt|apikey|session)""").containsMatchIn(text)
        ) {
            val i = lines.indexOfFirst { "\"math/rand" in it }
            if (i >= 0) hits += Finding(i + 1, "BLOCKING", lines[i].trim(),
                "math/rand is predictable. Anything secret (tokens, salts, session IDs, passwords) must come from crypto/rand.",
                "crypto/rand.Read(buf)  or  crypto/rand.Text() (Go 1.24+)", null)
        }
        // time.Sleep used to wait for goroutines
        if (Regex("""\bgo\s+(?:func\b|\w+\()""").containsMatchIn(text)) {
            val i = lines.indexOfFirst { Regex("""\btime\.Sleep\s*\(""").containsMatchIn(stripNoise(it)) }
            if (i >= 0) hits += Finding(i + 1, "MINOR", lines[i].trim(),
                "Sleeping to wait for goroutines is a guess, not a guarantee: on a busy machine the goroutine may not be done yet.",
                "sync.WaitGroup, a done channel, or errgroup.Wait()", "goroutines")
        }
        return hits.sortedWith(compareBy({ sevOrder[it.sev] ?: 9 }, { it.line }))
    }

    // ------------------------------------------------------- memory diagram
    fun renderMemory(rows: List<DiagramRow>, title: String?): String {
        val cols = listOf("name", "address", "size", "contents")
        val data = rows.map { listOf(it.name, it.addr, it.size, it.value) }
        val w = cols.indices.map { i -> maxOf(cols[i].length, data.maxOfOrNull { it[i].length } ?: 0) + 2 }
        fun center(s: String, n: Int): String {
            val pad = n - s.length
            if (pad <= 0) return s
            val left = pad / 2
            return " ".repeat(left) + s + " ".repeat(pad - left)
        }
        fun row(cells: List<String>) = cells.indices.joinToString("│") { center(cells[it], w[it]) }
        val sep = w.joinToString("┼") { "─".repeat(it) }
        val out = mutableListOf<String>()
        if (title != null) out += title
        out += row(cols)
        out += sep
        var lastFrame: String? = null
        rows.forEachIndexed { k, r ->
            if (r.frame.isNotEmpty() && r.frame != lastFrame) {
                if (lastFrame != null) out += sep
                lastFrame = r.frame
            }
            var line = row(data[k])
            if (r.note.isNotEmpty()) line += "  ──> " + r.note
            if (r.frame.isNotEmpty()) line += "  (${r.frame})"
            out += line
        }
        return out.joinToString("\n")
    }

    fun searchUrl(word: String): String =
        ui.searchEngine + java.net.URLEncoder.encode(ui.searchPrompt.replace("{word}", word.trim()), "UTF-8")
}

// ===== Drill.kt =====
/**
 * Port of the terminal trainer's scheduler (gotrainer drill/report):
 * SM-2-lite spacing plus interleaving that is enforced, not hoped for.
 */
class DrillScheduler(private val store: Store, private val items: List<DrillItem>) {
    private val state: JSONObject = store.obj("drill").also {
        if (!it.has("items")) it.put("items", JSONObject())
        if (!it.has("sessions")) it.put("sessions", 0)
    }

    private fun today() = LocalDate.now().toString()
    val sessions: Int get() = state.optInt("sessions")
    fun save() = store.saveObj("drill", state)

    fun itemState(id: String): JSONObject {
        val all = state.getJSONObject("items")
        if (!all.has(id)) {
            all.put(id, JSONObject().apply {
                put("ef", 2.5); put("reps", 0); put("interval", 0); put("due", today())
                put("seen", 0); put("lapses", 0); put("last", JSONObject.NULL)
            })
        }
        return all.getJSONObject(id)
    }

    fun peek(id: String): JSONObject? = state.getJSONObject("items").optJSONObject(id)

    /** grade: 0 again, 1 hard, 2 good, 3 easy. Returns the new interval in days. */
    fun reschedule(id: String, grade: Int): Int {
        val st = itemState(id)
        st.put("seen", st.optInt("seen") + 1)
        st.put("last", today())
        if (grade == 0) {
            st.put("reps", 0)
            st.put("lapses", st.optInt("lapses") + 1)
            st.put("interval", 0)
            st.put("ef", maxOf(1.3, st.optDouble("ef", 2.5) - 0.2))
            st.put("due", today())
            save()
            return 0
        }
        val q = mapOf(1 to 3, 2 to 4, 3 to 5)[grade] ?: 4
        val ef = maxOf(1.3, st.optDouble("ef", 2.5) + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)))
        st.put("ef", ef)
        val reps = st.optInt("reps") + 1
        st.put("reps", reps)
        var iv = when (reps) {
            1 -> 1
            2 -> 3
            else -> maxOf(1, Math.round(st.optInt("interval") * ef).toInt())
        }
        if (grade == 1) iv = maxOf(1, Math.round(iv * 0.6).toInt())
        st.put("interval", iv)
        st.put("due", LocalDate.now().plusDays(iv.toLong()).toString())
        save()
        return iv
    }

    fun dueItems(): List<DrillItem> {
        val t = today()
        return items.filter { val st = peek(it.id); st == null || st.optString("due") <= t }
    }

    fun nextDue(): String? {
        val all = state.getJSONObject("items")
        return all.keys().asSequence().map { all.getJSONObject(it).optString("due") }.minOrNull()
    }

    /** Consecutive items always come from different concepts when possible. */
    fun interleave(src: List<DrillItem>): List<DrillItem> {
        val pool = src.shuffled().toMutableList()
        val out = mutableListOf<DrillItem>()
        var last: String? = null
        while (pool.isNotEmpty()) {
            val pick = pool.firstOrNull { it.concept != last } ?: pool.first()
            pool.remove(pick)
            out += pick
            last = pick.concept
        }
        return out
    }

    fun finishSession() {
        state.put("sessions", sessions + 1)
        save()
    }

    fun reset() {
        state.put("items", JSONObject())
        state.put("sessions", 0)
        save()
    }

    data class ReportRow(
        val concept: String, val module: Int, val items: Int, val seen: Int, val lapses: Int,
        val last: String?, val status: String, val kind: String,
    )

    fun report(): List<ReportRow> {
        val t = LocalDate.now()
        return items.groupBy { it.concept }.map { (concept, its) ->
            val sts = its.mapNotNull { peek(it.id) }
            val seen = sts.sumOf { it.optInt("seen") }
            val lapses = sts.sumOf { it.optInt("lapses") }
            val last = sts.mapNotNull { if (it.isNull("last")) null else it.optString("last") }.maxOrNull()
            val (status, kind) = if (seen == 0) {
                "GAP - never retrieved" to "gap"
            } else {
                val days = (t.toEpochDay() - LocalDate.parse(last ?: t.toString()).toEpochDay()).toInt()
                val dueNow = its.count { val st = peek(it.id); st == null || st.optString("due") <= t.toString() }
                when {
                    dueNow > 0 -> "$dueNow due" to "due"
                    days > 21 -> "cold (${days}d)" to "cold"
                    else -> "warm" to "warm"
                }
            }
            ReportRow(concept, its.first().module, its.size, seen, lapses, last, status, kind)
        }.sortedBy { it.module }
    }
}

/** Leitner boxes for the Review tab (same intervals as the Python app). */
class Leitner(private val store: Store) {
    companion object {
        const val DAY = 86_400L
        const val MAX_BOX = 5
        val INTERVALS = mapOf(1 to 0L, 2 to 1 * DAY, 3 to 3 * DAY, 4 to 7 * DAY, 5 to 16 * DAY)
    }

    private val state: JSONObject = store.obj("review")
    private fun now() = System.currentTimeMillis() / 1000

    fun record(title: String): JSONObject? = state.optJSONObject(title)

    fun isDue(title: String): Boolean {
        val rec = record(title) ?: return true
        val box = rec.optInt("box", 1)
        return (now() - rec.optLong("last", 0)) >= (INTERVALS[box] ?: 0L)
    }

    fun update(title: String, gotIt: Boolean) {
        val rec = record(title) ?: JSONObject().apply { put("box", 1); put("seen", 0); put("correct", 0) }
        rec.put("seen", rec.optInt("seen") + 1)
        if (gotIt) {
            rec.put("correct", rec.optInt("correct") + 1)
            rec.put("box", minOf(rec.optInt("box", 1) + 1, MAX_BOX))
        } else {
            rec.put("box", 1)
        }
        rec.put("last", now())
        state.put(title, rec)
        store.saveObj("review", state)
    }

    /** learning (box 1-2), review (box 3), well-known (box 4-5) */
    fun summary(): Triple<Int, Int, Int> {
        val boxes = IntArray(MAX_BOX + 1)
        state.keys().forEach { k ->
            val b = state.optJSONObject(k)?.optInt("box", 1) ?: 1
            if (b in 1..MAX_BOX) boxes[b]++
        }
        return Triple(boxes[1] + boxes[2], boxes[3], boxes[4] + boxes[5])
    }

    fun intervalPhrase(box: Int): String {
        val secs = INTERVALS[box] ?: 0L
        if (secs <= 0) return "this session"
        val days = secs / DAY
        return if (days == 1L) "1 day" else "$days days"
    }
}

// ===== AppViewModel.kt =====
sealed class Screen {
    data object Home : Screen()
    data class LessonView(val index: Int) : Screen()
    data object Review : Screen()
    data object Diagrams : Screen()
    data object Drill : Screen()
    data object Report : Screen()
    data object Stats : Screen()
    data object Playground : Screen()
    data object About : Screen()
}

enum class LessonTab { Lesson, Quiz, Parsons, Check }

class AppViewModel(app: Application) : AndroidViewModel(app) {
    val course: Course = Course.load(app)
    val teaching = Teaching(course)
    val store = Store(app)
    val engage = Engage(store, course.ui.ranks)
    val go = GoToolchain(app, course.ui.errorHints)
    val drill = DrillScheduler(store, course.drillItems)
    val leitner = Leitner(store)

    // ---------------------------------------------------------- navigation
    var screen by mutableStateOf<Screen>(Screen.Home)
        private set
    private val backStack = ArrayDeque<Screen>()

    fun open(s: Screen) {
        if (s != screen) backStack.addLast(screen)
        screen = s
    }

    /** Returns false when there is nowhere to go back to. */
    fun back(): Boolean {
        val prev = backStack.removeLastOrNull() ?: return false
        screen = prev
        if (prev is Screen.LessonView) loadLesson(prev.index, push = false)
        return true
    }

    // ------------------------------------------------------------ toolchain
    var installProgress by mutableStateOf(0f)
    var installMessage by mutableStateOf("")
    var installError by mutableStateOf<String?>(null)
    var goReady by mutableStateOf(false)

    fun startInstall() {
        if (goReady || !go.abiSupported || !go.bundled) return
        installError = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                go.install { msg, p ->
                    viewModelScope.launch(Dispatchers.Main) { installMessage = msg; installProgress = p }
                }
                withContext(Dispatchers.Main) { goReady = go.installed() }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    installError = "Could not unpack Go: ${e.message ?: e.javaClass.simpleName}. " +
                        "Check that the phone has about 1 GB of free space, then tap Retry."
                }
            }
        }
    }

    // ------------------------------------------------------------- progress
    var completed by mutableStateOf<Set<String>>(store.completed())
        private set
    var engageSummary by mutableStateOf(engage.summary())
        private set
    var snack by mutableStateOf<List<String>?>(null)
    var status by mutableStateOf("")

    val doneCount: Int get() = completed.count { t -> course.lesson(t) != null }

    private fun rewardIfNew(title: String) {
        val l = course.lesson(title) ?: return
        val (gain, msgs0) = engage.record(l.boss, title)
        if (gain == 0 && msgs0.isEmpty()) return
        val msgs = msgs0.toMutableList()
        if (l.boss) {
            val nxt = course.lessons.firstOrNull { it.anatomy && it.module == l.module + 1 }
            msgs.add(0, "Level cleared!" + (nxt?.let { " Next: " + course.modules[it.module] } ?: " You finished Go Anatomy!"))
        }
        val head = "+$gain XP   ♨ ${engage.streakAlive()}-day streak   today ${engage.todayCount}/${engage.goal}"
        snack = listOf(head) + msgs
        status = head + (msgs.firstOrNull()?.let { "   ·   $it" } ?: "")
        engageSummary = engage.summary()
    }

    fun completeCurrent(msg: String) {
        val t = lesson.title
        if (t !in completed) {
            completed = completed + t
            store.saveCompleted(completed)
            status = msg
            rewardIfNew(t)
        } else {
            status = msg
        }
    }

    fun toggleComplete() {
        val t = lesson.title
        if (t in completed) {
            completed = completed - t
            store.saveCompleted(completed)
            status = "Marked not complete - progress saved."
        } else {
            completed = completed + t
            store.saveCompleted(completed)
            status = "Lesson complete - progress saved."
            rewardIfNew(t)
        }
    }

    fun setGoal(n: Int) {
        engage.setGoal(n)
        engageSummary = engage.summary()
    }

    // ---------------------------------------------------------- lesson view
    var lessonIndex by mutableStateOf(0)
        private set
    val lesson: Lesson get() = course.lessons[lessonIndex]
    var tab by mutableStateOf(LessonTab.Lesson)
    var code by mutableStateOf(TextFieldValue(""))
    var stdin by mutableStateOf("")
    var predict by mutableStateOf("")
    var output by mutableStateOf("")
    var outputKind by mutableStateOf("none")   // none, ok, error, warn, muted
    var outputStatus by mutableStateOf("")
    var running by mutableStateOf(false)
        private set
    var solutionShown by mutableStateOf(false)
        private set
    var note by mutableStateOf("")
        private set
    var fontSize by mutableStateOf(store.int("concept_font", 15))
        private set
    var lastLessonIndex by mutableStateOf(store.int("last_lesson", 0))
        private set

    private val notes = store.obj("notes")

    fun openLesson(i: Int) {
        loadLesson(i, push = true)
    }

    private fun loadLesson(i: Int, push: Boolean) {
        val idx = i.coerceIn(0, course.lessons.lastIndex)
        lessonIndex = idx
        lastLessonIndex = idx
        store.saveInt("last_lesson", idx)
        val l = course.lessons[idx]
        solutionShown = false
        code = TextFieldValue(if (l.isChallenge) teaching.starter(l) else l.code)
        stdin = l.stdin
        predict = ""
        output = ""
        outputKind = "none"
        outputStatus = ""
        tab = LessonTab.Lesson
        note = notes.optString(l.title, "")
        status = if (go.notReadyReason() == null) "● ${go.versionText} ready - builds and runs offline" else ""
        val target = Screen.LessonView(idx)
        if (push) open(target) else screen = target
        if (l.isBranch) exploreReward(l.title)
    }

    private fun exploreReward(title: String) {
        val key = "✦explore:$title"
        if (engage.wasRewarded(key)) return
        val (gain, _) = engage.record(false, key)
        if (gain > 0) {
            engageSummary = engage.summary()
            status = "Explored a new branch  ✦  +$gain XP  ·  $engageSummary"
        }
    }

    fun next() { if (lessonIndex < course.lessons.lastIndex) loadLesson(lessonIndex + 1, push = false) }
    fun prev() { if (lessonIndex > 0) loadLesson(lessonIndex - 1, push = false) }

    fun goDeeper() {
        val kids = lesson.branchChildren.mapNotNull { course.lesson(it) }
        if (kids.isEmpty()) { status = "No deeper branch here - this is a leaf."; return }
        val target = kids.firstOrNull { it.title !in completed } ?: kids.first()
        collapsed = collapsed - lesson.title
        loadLesson(target.index, push = false)
    }

    fun backUp() {
        val p = course.lesson(lesson.branchParent)
        if (p == null) { status = "Already at the top of the tree."; return }
        loadLesson(p.index, push = false)
    }

    fun saveNote(text: String) {
        note = text
        if (text.isBlank()) notes.remove(lesson.title) else notes.put(lesson.title, text)
        store.saveObj("notes", notes)
    }

    fun bumpFont(delta: Int) {
        fontSize = (fontSize + delta).coerceIn(9, 28)
        store.saveInt("concept_font", fontSize)
    }

    fun resetFont() {
        fontSize = 15
        store.saveInt("concept_font", fontSize)
    }

    fun resetCode() {
        val l = lesson
        code = TextFieldValue(if (l.isChallenge && !solutionShown) teaching.starter(l) else l.code)
        output = ""
        outputKind = "none"
        outputStatus = ""
    }

    fun showSolution() {
        if (!lesson.isChallenge) return
        solutionShown = true
        code = TextFieldValue(lesson.code)
        status = "Solution loaded - study it, then Reset and write it yourself."
    }

    private fun predictionMatches(pred: String, actual: String): Boolean {
        fun norm(s: String) = s.split(Regex("\\s+")).filter { it.isNotEmpty() }.joinToString(" ").lowercase()
        val p = norm(pred)
        val a = norm(actual)
        return p.isNotEmpty() && (p == a || a.contains(p))
    }

    fun run() {
        if (running) return
        val l = lesson
        val src = code.text
        if (src.isBlank()) {
            output = "This is a concept lesson - there's no code to run.\nRead the explanation, then take the quiz or press 'Mark complete'."
            outputKind = "muted"
            return
        }
        running = true
        output = "Building and running with ${go.versionText}… (on the phone, offline)"
        outputKind = "muted"
        outputStatus = "running…"
        val runIdx = lessonIndex
        val pred = predict.trim()
        val stdinText = if (stdin.isNotEmpty() && !stdin.endsWith("\n")) stdin + "\n" else stdin
        viewModelScope.launch {
            val res = withContext(Dispatchers.IO) { go.run(src, stdinText, l.flags, l.files, l.expected) }
            running = false
            if (runIdx != lessonIndex) return@launch
            showResult(res, pred)
        }
    }

    private fun showResult(res: RunResult, pred: String) {
        val l = lesson
        val notePrefix = res.note?.let { "» $it\n\n" } ?: ""
        when (res.status) {
            "ok" -> {
                var banner = ""
                if (pred.isNotEmpty()) {
                    banner = if (predictionMatches(pred, res.text)) "» Prediction ✓ matched the output.\n\n"
                    else "» Prediction differs from the actual output below - compare them; that gap is " +
                        "exactly where your mental model needs adjusting.\n\n"
                }
                output = notePrefix + banner + res.text
                outputKind = "ok"
                outputStatus = "ran OK"
                val mode = teaching.checkMode(l)
                val exp = l.expected
                val passed = when {
                    mode == "strict" && exp != null -> res.text.trim() == exp.trim()
                    mode == "runs" -> res.text.isNotBlank() && !solutionShown
                    else -> false
                }
                if (passed) {
                    completeCurrent("✓  Challenge passed - your output matches the target.")
                } else if (mode == "strict" && exp != null && !solutionShown) {
                    status = "Runs, but output doesn't match the target yet - compare carefully and adjust."
                    outputStatus = "output mismatch"
                    outputKind = "warn"
                }
            }
            "compile_error" -> { output = notePrefix + "COMPILE ERROR\n\n" + res.text; outputKind = "error"; outputStatus = "compile error" }
            "test_fail" -> { output = notePrefix + "TESTS FAILED\n\n" + res.text; outputKind = "error"; outputStatus = "tests failed" }
            "needs_newer_go" -> { output = "NEEDS A NEWER GO\n\n" + res.text; outputKind = "warn"; outputStatus = "needs newer Go" }
            "needs_cgo" -> { output = "NEEDS A C COMPILER\n\n" + res.text; outputKind = "warn"; outputStatus = "cgo lesson" }
            "timeout" -> { output = notePrefix + res.text; outputKind = "warn"; outputStatus = "timeout" }
            else -> { output = res.text; outputKind = "error"; outputStatus = res.status }
        }
    }

    // --------------------------------------------------------------- quiz
    var quizChoice by mutableStateOf(-1)
    var quizChecked by mutableStateOf<Int?>(null)

    // ---------------------------------------------------------- check tab
    var checkRunning by mutableStateOf(false)
        private set
    var checkFindings by mutableStateOf<List<Teaching.Finding>?>(null)
        private set
    var vetOutput by mutableStateOf<String?>(null)
        private set

    fun runCheck() {
        if (checkRunning) return
        val src = code.text
        checkFindings = teaching.lint(src)
        vetOutput = null
        if (src.isBlank()) return
        checkRunning = true
        viewModelScope.launch {
            val v = withContext(Dispatchers.IO) { go.vet(src) }
            vetOutput = v ?: "(go vet unavailable: ${go.notReadyReason() ?: "unknown"})"
            checkRunning = false
        }
    }

    // ------------------------------------------------------------ list UI
    var search by mutableStateOf("")
    var collapsed by mutableStateOf<Set<String>>(emptySet())

    fun toggleCollapse(title: String) {
        collapsed = if (title in collapsed) collapsed - title else collapsed + title
    }

    // --------------------------------------------------------- playground
    var playCode by mutableStateOf(TextFieldValue(PLAY_DEFAULT))
    var playArgs by mutableStateOf("run .")
    var playStdin by mutableStateOf("")
    var playOut by mutableStateOf("")
    var playRunning by mutableStateOf(false)
        private set

    fun runPlayground() {
        if (playRunning) return
        go.notReadyReason()?.let { playOut = it; return }
        playRunning = true
        playOut = "$ go ${playArgs.trim()}\n"
        val args = Regex("""[^\s"']+|"([^"]*)"|'([^']*)'""").findAll(playArgs.trim()).map { m ->
            m.groups[1]?.value ?: m.groups[2]?.value ?: m.value
        }.toList()
        val src = playCode.text
        val input = playStdin.let { if (it.isNotEmpty() && !it.endsWith("\n")) it + "\n" else it }
        viewModelScope.launch {
            val r = withContext(Dispatchers.IO) { go.goCommand(args, src, input) }
            var s = "$ go ${playArgs.trim()}\n" + r.out
            if (r.err.isNotEmpty()) s += (if (r.out.isEmpty() || r.out.endsWith("\n")) "" else "\n") + r.err
            if (r.timedOut) s += "\n[stopped: took too long]"
            else if (r.code != 0) s += "\n[exit status ${r.code}]"
            playOut = s
            playRunning = false
        }
    }

    companion object {
        const val PLAY_DEFAULT = "package main\n\nimport (\n\t\"fmt\"\n\t\"runtime\"\n)\n\nfunc main() {\n" +
            "\tfmt.Println(\"Hello from Go on my phone!\")\n" +
            "\tfmt.Println(runtime.Version(), runtime.GOOS+\"/\"+runtime.GOARCH)\n}\n"
    }

    init {
        goReady = go.installed()
        if (!goReady) startInstall()
    }
}

// ===== MainActivity.kt =====
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoTheme {
                val vm: AppViewModel = viewModel()
                AppRoot(vm)
            }
        }
    }
}

@Composable
fun AppRoot(vm: AppViewModel) {
    BackHandler(enabled = vm.screen != Screen.Home) {
        if (!vm.back()) vm.open(Screen.Home)
    }
    Box(Modifier.fillMaxSize().background(C.BG)) {
        when (vm.screen) {
            is Screen.Home -> HomeScreen(vm)
            is Screen.LessonView -> LessonScreen(vm)
            is Screen.Review -> ReviewScreen(vm)
            is Screen.Diagrams -> DiagramsScreen(vm)
            is Screen.Drill -> DrillScreen(vm)
            is Screen.Report -> ReportScreen(vm)
            is Screen.Stats -> StatsScreen(vm)
            is Screen.Playground -> PlaygroundScreen(vm)
            is Screen.About -> AboutScreen(vm)
        }
        val lines = vm.snack
        if (lines != null) {
            LaunchedEffect(lines) {
                delay(3800)
                vm.snack = null
            }
            Column(
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .background(C.BG, RoundedCornerShape(10.dp))
                    .border(2.dp, C.ACCENT2, RoundedCornerShape(10.dp))
                    .clickable { vm.snack = null }
                    .padding(16.dp)
            ) {
                lines.forEachIndexed { i, s ->
                    Text(
                        s, color = if (i == 0) C.OK else C.FG, fontSize = 14.sp,
                        fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}
