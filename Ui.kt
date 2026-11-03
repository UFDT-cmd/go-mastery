// Go Mastery for Android - generated from Go_course_v3.py.
// Upload this file unchanged to the root of your GitHub repository.

package com.gomastery.app.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gomastery.app.AppViewModel
import com.gomastery.app.Course
import com.gomastery.app.DrillItem
import com.gomastery.app.Leitner
import com.gomastery.app.LessonTab
import com.gomastery.app.Quiz
import com.gomastery.app.Screen
import kotlinx.coroutines.delay

// ===== Theme.kt =====
// Same palette as the Python app.
object C {
    val BG = Color(0xFF111827)
    val PANEL = Color(0xFF161E30)
    val PANEL2 = Color(0xFF1C2640)
    val LINE = Color(0xFF263248)
    val FG = Color(0xFFDDE3ED)
    val MUTED = Color(0xFF7A8599)
    val ACCENT = Color(0xFF00ADD8)
    val ACCENT2 = Color(0xFFF59E0B)
    val OK = Color(0xFF6DBE8D)
    val ERR = Color(0xFFE06C75)
    val CODEBG = Color(0xFF0B0F1C)
    val SELBG = Color(0xFF1A3460)
    val OKBG = Color(0xFF1A3325)
    val ERRBG = Color(0xFF2E1A1A)

    // syntax colours
    val KW = Color(0xFFC678DD)
    val TYPE = Color(0xFF56B6C2)
    val FUNC = Color(0xFF61AFEF)
    val STR = Color(0xFF98C379)
    val NUM = Color(0xFFD19A66)
    val COMMENT = Color(0xFF5C6370)
}

val Mono = FontFamily.Monospace

@Composable
fun GoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = C.ACCENT,
            onPrimary = C.BG,
            secondary = C.ACCENT2,
            onSecondary = C.BG,
            background = C.BG,
            onBackground = C.FG,
            surface = C.PANEL,
            onSurface = C.FG,
            surfaceVariant = C.PANEL2,
            onSurfaceVariant = C.MUTED,
            outline = C.LINE,
            error = C.ERR,
        ),
        content = content,
    )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier, color: Color = C.ACCENT2) {
    Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = modifier)
}

@Composable
fun AccentButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, color: Color = C.ACCENT, onClick: () -> Unit) {
    Button(
        onClick = onClick, enabled = enabled, modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = C.BG),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
    ) { Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
}

@Composable
fun GhostButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick, enabled = enabled, modifier = modifier,
        border = BorderStroke(1.dp, C.LINE),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = C.FG),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
    ) { Text(text, fontSize = 14.sp) }
}

@Composable
fun Panel(modifier: Modifier = Modifier, bg: Color = C.PANEL, border: Color = C.LINE, content: @Composable () -> Unit) {
    Box(
        modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(8.dp))
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) { content() }
}

val codeStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, lineHeight = 19.sp, color = C.FG)

// ===== CodeEditor.kt =====
/**
 * Go syntax highlighting. Tabs stay real tabs in the stored text (several
 * lessons depend on them) but are drawn 4 columns wide, with an offset mapping
 * so the cursor and selection still line up.
 */
class GoHighlighter(
    private val keywords: Set<String>,
    private val types: Set<String>,
    private val funcs: Set<String>,
) : VisualTransformation {

    private val token = Regex(
        """//[^\n]*|/\*[\s\S]*?(?:\*/|$)|"(?:\\.|[^"\\\n])*"?|`[^`]*`?|'(?:\\.|[^'\\\n])*'?|\b\d[\w.]*|[A-Za-z_]\w*"""
    )
    private val literals = setOf("true", "false", "nil", "iota")

    fun highlight(display: String): AnnotatedString {
        val b = AnnotatedString.Builder(display)
        for (m in token.findAll(display)) {
            val v = m.value
            val r = m.range
            val style: SpanStyle? = when {
                v.startsWith("//") || v.startsWith("/*") -> SpanStyle(color = C.COMMENT, fontStyle = FontStyle.Italic)
                v[0] == '"' || v[0] == '`' || v[0] == '\'' -> SpanStyle(color = C.STR)
                v[0].isDigit() -> SpanStyle(color = C.NUM)
                v in keywords -> SpanStyle(color = C.KW, fontWeight = FontWeight.Bold)
                v in literals -> SpanStyle(color = C.NUM)
                v in types -> SpanStyle(color = C.TYPE)
                v in funcs || nextIsParen(display, r.last + 1) -> SpanStyle(color = C.FUNC)
                else -> null
            }
            if (style != null) b.addStyle(style, r.first, r.last + 1)
        }
        return b.toAnnotatedString()
    }

    private fun nextIsParen(s: String, from: Int): Boolean {
        var i = from
        while (i < s.length && s[i] == ' ') i++
        return i < s.length && s[i] == '('
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val src = text.text
        val n = src.length
        // starts[o] = display offset of original offset o
        val starts = IntArray(n + 1)
        var shift = 0
        for (i in 0 until n) {
            starts[i] = i + shift
            if (src[i] == '\t') shift += 3
        }
        starts[n] = n + shift
        val display = if (shift == 0) src else src.replace("\t", "    ")
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = starts[offset.coerceIn(0, n)]
            override fun transformedToOriginal(offset: Int): Int {
                var lo = 0
                var hi = n
                while (lo < hi) {
                    val mid = (lo + hi + 1) ushr 1
                    if (starts[mid] <= offset) lo = mid else hi = mid - 1
                }
                return lo
            }
        }
        return TransformedText(highlight(display), mapping)
    }
}

/** Insert text at the cursor (replacing any selection). */
fun TextFieldValue.insert(s: String): TextFieldValue {
    val start = minOf(selection.start, selection.end).coerceIn(0, text.length)
    val end = maxOf(selection.start, selection.end).coerceIn(0, text.length)
    val t = text.substring(0, start) + s + text.substring(end)
    return TextFieldValue(t, TextRange(start + s.length))
}

/** Auto-indent: a new line copies the previous line's indentation (+1 tab after '{'). */
fun smartEdit(old: TextFieldValue, new: TextFieldValue): TextFieldValue {
    val sel = new.selection
    if (new.text.length != old.text.length + 1 || !sel.collapsed) return new
    val c = sel.start
    if (c <= 0 || c > new.text.length || new.text[c - 1] != '\n') return new
    if (new.text.removeRange(c - 1, c) != old.text) return new
    val lineStart = new.text.lastIndexOf('\n', c - 2) + 1
    val prev = new.text.substring(lineStart, c - 1)
    var indent = prev.takeWhile { it == '\t' || it == ' ' }
    val trimmed = prev.trimEnd()
    if (trimmed.endsWith("{") || trimmed.endsWith("(")) indent += "\t"
    if (indent.isEmpty()) return new
    val t = new.text.substring(0, c) + indent + new.text.substring(c)
    return TextFieldValue(t, TextRange(c + indent.length))
}

@Composable
fun CodeEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    highlighter: VisualTransformation,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    minHeight: Int = 160,
    fontSize: Int = 13,
) {
    val style = codeStyle.copy(fontSize = fontSize.sp, lineHeight = (fontSize * 1.5f).sp)
    val lines = value.text.count { it == '\n' } + 1
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .heightIn(min = minHeight.dp)
            .background(C.CODEBG, RoundedCornerShape(8.dp))
            .border(1.dp, C.LINE, RoundedCornerShape(8.dp))
    ) {
        val full = maxWidth
        Row(Modifier.padding(vertical = 8.dp)) {
            Text(
                (1..lines).joinToString("\n"),
                style = style.copy(color = C.LINE, textAlign = TextAlign.End),
                modifier = Modifier.width((10 + 9 * lines.toString().length).dp).padding(end = 6.dp),
            )
            Box(Modifier.horizontalScroll(rememberScrollState())) {
                BasicTextField(
                    value = value,
                    onValueChange = { nv -> onValueChange(smartEdit(value, nv)) },
                    readOnly = readOnly,
                    textStyle = style,
                    cursorBrush = SolidColor(C.ACCENT),
                    visualTransformation = highlighter,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Ascii,
                    ),
                    modifier = Modifier
                        .widthIn(min = (full - (16 + 9 * lines.toString().length).dp).coerceAtLeast(0.dp))
                        .heightIn(min = (minHeight - 16).dp)
                        .padding(end = 12.dp),
                )
            }
        }
    }
}

/** Keys a phone keyboard makes awkward to reach. */
@Composable
fun SymbolBar(onKey: (String) -> Unit) {
    val keys = listOf("⇥", "{", "}", "(", ")", "[", "]", "\"", ":=", "=", ":", ";", ".", ",", "*", "&", "<-", "!", "|", "+", "-", "/", "%", "<", ">", "_", "`", "'")
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp)
    ) {
        keys.forEach { k ->
            Box(
                Modifier
                    .padding(end = 4.dp)
                    .background(C.PANEL2, RoundedCornerShape(6.dp))
                    .border(1.dp, C.LINE, RoundedCornerShape(6.dp))
                    .clickable { onKey(if (k == "⇥") "\t" else k) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) { Text(k, style = codeStyle.copy(fontSize = 15.sp)) }
        }
        Spacer(Modifier.width(8.dp))
    }
}

// ===== HomeScreen.kt =====
/** One row of the lesson list: a module header or a lesson (possibly a tree node). */
data class ListRow(val header: String?, val index: Int, val depth: Int, val hasKids: Boolean, val collapsed: Boolean)

/**
 * Same layout rules as the Python lesson list: module headers, and in Go Anatomy
 * modules the branch tree (indent + ▾/▸ collapse). A search shows a flat filter.
 */
fun buildRows(course: Course, search: String, collapsed: Set<String>): List<ListRow> {
    val out = ArrayList<ListRow>()
    val q = search.trim().lowercase()
    var lastMod = -1
    if (q.isNotEmpty()) {
        course.lessons.forEach { l ->
            if (q !in l.title.lowercase() && q !in l.concept.lowercase()) return@forEach
            if (l.module != lastMod) {
                out += ListRow(course.modules[l.module].uppercase(), -1, 0, false, false)
                lastMod = l.module
            }
            out += ListRow(null, l.index, 0, false, false)
        }
        return out
    }
    val anat = course.lessons.filter { it.anatomy }.map { it.module }.toSet()
    val seen = HashSet<Int>()
    fun kids(i: Int) = course.lessons[i].branchChildren.mapNotNull { course.lesson(it)?.index }
    fun walk(i: Int, depth: Int) {
        if (!seen.add(i)) return
        val l = course.lessons[i]
        val k = kids(i)
        val isCollapsed = l.title in collapsed
        out += ListRow(null, i, depth, k.isNotEmpty(), isCollapsed)
        if (isCollapsed) return
        k.forEach { j -> if (course.lessons[j].module == l.module) walk(j, depth + 1) }
    }
    course.lessons.forEach { l ->
        if (l.module != lastMod) {
            out += ListRow(course.modules[l.module].uppercase(), -1, 0, false, false)
            lastMod = l.module
        }
        if (l.module in anat) {
            if (l.branchParent == null && l.index !in seen) walk(l.index, 0)
        } else {
            out += ListRow(null, l.index, 0, false, false)
        }
    }
    return out
}

@Composable
fun InstallBanner(vm: AppViewModel) {
    val go = vm.go
    val reason = if (!go.abiSupported || !go.bundled) go.notReadyReason() else null
    when {
        reason != null -> Panel(border = C.ERR) { Text(reason, color = C.ERR, fontSize = 13.sp) }
        vm.installError != null -> Panel(border = C.ERR) {
            Column {
                Text(vm.installError ?: "", color = C.ERR, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                AccentButton("Retry") { vm.startInstall() }
            }
        }
        !vm.goReady -> Panel(border = C.ACCENT) {
            Column {
                Text("Setting up the Go compiler on your phone (one time only)…", color = C.FG, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { vm.installProgress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(), color = C.ACCENT, trackColor = C.LINE,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${(vm.installProgress * 100).toInt()}%  ${vm.installMessage}", color = C.MUTED, fontSize = 11.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                Text("You can start reading lessons now; Run works as soon as this finishes.", color = C.MUTED, fontSize = 11.sp)
            }
        }
        else -> Text("● ${go.versionText} ready — compiles and runs offline on this phone", color = C.OK, fontSize = 12.sp)
    }
}

@Composable
fun HomeScreen(vm: AppViewModel) {
    val course = vm.course
    val rows = remember(vm.search, vm.collapsed) { buildRows(course, vm.search, vm.collapsed) }
    val n = course.lessons.size
    val done = vm.doneCount
    val pct = if (n > 0) (100f * done / n) else 0f
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().background(C.BG),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
    ) {
        item {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Go Mastery", color = C.FG, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(vm.engageSummary, color = C.ACCENT2, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$done / $n  ·  ${pct.toInt()}%", color = C.MUTED, fontSize = 12.sp)
                    Spacer(Modifier.width(10.dp))
                    LinearProgressIndicator(
                        progress = { if (n > 0) done.toFloat() / n else 0f },
                        modifier = Modifier.weight(1f).height(6.dp), color = C.OK, trackColor = C.LINE,
                    )
                }
                InstallBanner(vm)
                val cont = course.lessons[vm.lastLessonIndex.coerceIn(0, n - 1)]
                AccentButton("▶  Continue: ${cont.title}", modifier = Modifier.fillMaxWidth()) { vm.openLesson(cont.index) }
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    GhostButton("Review") { vm.open(Screen.Review) }
                    GhostButton("Drill") { vm.open(Screen.Drill) }
                    GhostButton("Report") { vm.open(Screen.Report) }
                    GhostButton("Diagrams") { vm.open(Screen.Diagrams) }
                    GhostButton("Stats") { vm.open(Screen.Stats) }
                    GhostButton("Go terminal") { vm.open(Screen.Playground) }
                    GhostButton("About") { vm.open(Screen.About) }
                }
                OutlinedTextField(
                    value = vm.search,
                    onValueChange = { vm.search = it },
                    placeholder = { Text("Search lessons…", color = C.MUTED) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = C.ACCENT, unfocusedBorderColor = C.LINE,
                        focusedContainerColor = C.PANEL2, unfocusedContainerColor = C.PANEL2,
                        focusedTextColor = C.FG, unfocusedTextColor = C.FG, cursorColor = C.ACCENT,
                    ),
                )
                if (vm.status.isNotEmpty()) Text(vm.status, color = C.OK, fontSize = 12.sp)
            }
        }
        items(rows) { r ->
            if (r.header != null) {
                Text(
                    r.header, color = C.ACCENT2, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 4.dp),
                )
            } else {
                val l = course.lessons[r.index]
                val isDone = l.title in vm.completed
                val current = r.index == vm.lastLessonIndex
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(if (current) C.SELBG else C.BG)
                        .clickable { vm.openLesson(r.index) }
                        .padding(start = (16 + 18 * r.depth).dp, end = 16.dp, top = 9.dp, bottom = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (r.hasKids) {
                        Text(
                            if (r.collapsed) "▸" else "▾", color = C.ACCENT, fontSize = 16.sp,
                            modifier = Modifier.clickable { vm.toggleCollapse(l.title) }.padding(end = 8.dp),
                        )
                    } else if (r.depth > 0 || l.anatomy) {
                        Spacer(Modifier.width(18.dp))
                    }
                    Text(if (isDone) "✓" else "○", color = if (isDone) C.OK else C.MUTED, fontSize = 14.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        l.title, color = if (isDone) C.OK else C.FG, fontSize = 14.sp,
                        fontWeight = if (l.boss) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

// ===== LessonScreen.kt =====
fun openUrl(ctx: Context, url: String) {
    try {
        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: ActivityNotFoundException) {
        // no browser installed
    }
}

@Composable
fun fieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = C.ACCENT, unfocusedBorderColor = C.LINE,
    focusedContainerColor = C.PANEL2, unfocusedContainerColor = C.PANEL2,
    focusedTextColor = C.FG, unfocusedTextColor = C.FG, cursorColor = C.ACCENT,
    focusedLabelColor = C.ACCENT, unfocusedLabelColor = C.MUTED,
)

fun youtubeFor(title: String): String =
    "https://www.youtube.com/results?search_query=" + java.net.URLEncoder.encode("golang $title", "UTF-8")

@Composable
fun LessonScreen(vm: AppViewModel) {
    val ctx = LocalContext.current
    val l = vm.lesson
    val n = vm.course.lessons.size
    var menu by remember { mutableStateOf(false) }
    var showMovie by remember { mutableStateOf(false) }
    var showLookup by remember { mutableStateOf(false) }

    fun playMovie() {
        if (l.movie.isEmpty()) vm.status = "This lesson has no micro-movie. Press F8 (or ⋮ → Video) for a short video."
        else showMovie = true
    }
    fun openVideo() {
        openUrl(ctx, l.video ?: youtubeFor(l.title))
        vm.status = "Opened a short-video search in your browser."
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(C.BG)
            .onPreviewKeyEvent { e ->
                if (e.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                val ctrl = e.isCtrlPressed
                when {
                    ctrl && (e.key == Key.Enter || e.key == Key.NumPadEnter) -> { vm.run(); true }
                    ctrl && e.key == Key.DirectionRight -> { vm.next(); true }
                    ctrl && e.key == Key.DirectionLeft -> { vm.prev(); true }
                    ctrl && e.key == Key.M -> { vm.toggleComplete(); true }
                    ctrl && (e.key == Key.Equals || e.key == Key.Plus || e.key == Key.NumPadAdd) -> { vm.bumpFont(1); true }
                    ctrl && (e.key == Key.Minus || e.key == Key.NumPadSubtract) -> { vm.bumpFont(-1); true }
                    ctrl && e.key == Key.Zero -> { vm.resetFont(); true }
                    ctrl && e.key == Key.B -> { showLookup = true; true }
                    e.key == Key.F7 -> { playMovie(); true }
                    e.key == Key.F8 -> { openVideo(); true }
                    else -> false
                }
            }
    ) {
        // ── header ──
        Row(
            Modifier.fillMaxWidth().background(C.PANEL).padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { if (!vm.back()) vm.open(Screen.Home) }) { Text("←", color = C.FG, fontSize = 20.sp) }
            Column(Modifier.weight(1f)) {
                Text(
                    "[${l.index + 1} / $n]  ${l.title}", color = C.FG, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                )
                Text(vm.course.modules[l.module], color = C.MUTED, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Box {
                TextButton(onClick = { menu = true }) { Text("⋮", color = C.FG, fontSize = 20.sp) }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("▶ Micro-movie  (F7)") }, onClick = { menu = false; playMovie() })
                    DropdownMenuItem(text = { Text("Short video  (F8)") }, onClick = { menu = false; openVideo() })
                    DropdownMenuItem(text = { Text("Look up a word  (Ctrl+B)") }, onClick = { menu = false; showLookup = true })
                    DropdownMenuItem(text = { Text("Memory diagrams") }, onClick = { menu = false; vm.open(Screen.Diagrams) })
                    DropdownMenuItem(text = { Text("Review deck") }, onClick = { menu = false; vm.open(Screen.Review) })
                    DropdownMenuItem(text = { Text("All lessons") }, onClick = { menu = false; vm.open(Screen.Home) })
                }
            }
        }
        // ── tabs ──
        val tabs = LessonTab.values()
        TabRow(
            selectedTabIndex = vm.tab.ordinal, containerColor = C.PANEL, contentColor = C.ACCENT,
            indicator = { pos ->
                TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(pos[vm.tab.ordinal]), color = C.ACCENT)
            },
        ) {
            tabs.forEach { t ->
                Tab(
                    selected = vm.tab == t, onClick = { vm.tab = t },
                    text = { Text(t.name, color = if (vm.tab == t) C.ACCENT else C.MUTED, fontSize = 13.sp) },
                )
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (vm.tab) {
                LessonTab.Lesson -> LessonTabContent(vm)
                LessonTab.Quiz -> QuizTab(vm)
                LessonTab.Parsons -> ParsonsTab(vm)
                LessonTab.Check -> CheckTab(vm)
            }
        }
        // ── status + navigation ──
        if (vm.status.isNotEmpty()) {
            Text(
                vm.status, color = C.OK, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().background(C.PANEL).padding(horizontal = 12.dp, vertical = 4.dp),
            )
        }
        val done = l.title in vm.completed
        Row(
            Modifier.fillMaxWidth().background(C.PANEL).padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically,
        ) {
            GhostButton("◂ Prev", enabled = l.index > 0) { vm.prev() }
            AccentButton(
                if (done) "Completed ✓" else "Mark complete", modifier = Modifier.weight(1f),
                color = if (done) C.OK else C.ACCENT2,
            ) { vm.toggleComplete() }
            GhostButton("Next ▸", enabled = l.index < n - 1) { vm.next() }
        }
    }

    if (showMovie) MovieDialog(l.title, l.movie) { showMovie = false }
    if (showLookup) LookupDialog(vm, onDismiss = { showLookup = false })
}

@Composable
fun MovieDialog(title: String, frames: List<String>, onDismiss: () -> Unit) {
    var shown by remember { mutableIntStateOf(0) }
    var run by remember { mutableIntStateOf(0) }
    LaunchedEffect(run) {
        shown = 0
        for (k in frames.indices) {
            shown = k + 1
            delay(1100)
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = C.BG,
        title = { Text("▶ MICRO-MOVIE: $title", color = C.ACCENT2, fontSize = 15.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                for (k in 0 until shown) {
                    Text("[${k + 1}] ${frames[k]}", style = codeStyle, modifier = Modifier.padding(bottom = 6.dp))
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    if (shown < frames.size) "frame $shown of ${frames.size}" else "The end. Tap Replay to watch again.",
                    color = C.MUTED, fontSize = 11.sp,
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        dismissButton = { TextButton(onClick = { run++ }) { Text("Replay") } },
    )
}

@Composable
fun LookupDialog(vm: AppViewModel, onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    var word by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = C.PANEL,
        title = { Text("Look up a word", color = C.FG, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Type a word you don't understand:", color = C.FG, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = word, onValueChange = { word = it }, singleLine = true,
                    textStyle = codeStyle, colors = fieldColors(), modifier = Modifier.fillMaxWidth(),
                )
                Text("Opens a search in your browser · keyboard shortcut: Ctrl+B", color = C.MUTED, fontSize = 11.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (word.isNotBlank()) openUrl(ctx, vm.teaching.searchUrl(word))
                onDismiss()
            }) { Text("Search") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

/** Concept text with the ▸ section headings picked out, like the desktop app. */
@Composable
fun ConceptText(text: String, size: Int) {
    val ann = remember(text) {
        buildAnnotatedString {
            text.split("\n").forEachIndexed { i, line ->
                if (i > 0) append("\n")
                if (line.startsWith("▸")) {
                    withStyle(SpanStyle(color = C.ACCENT2, fontWeight = FontWeight.Bold)) { append(line) }
                } else {
                    append(line)
                }
            }
        }
    }
    SelectionContainer {
        Text(ann, color = C.FG, fontSize = size.sp, lineHeight = (size * 1.45f).sp)
    }
}

@Composable
fun LessonTabContent(vm: AppViewModel) {
    val l = vm.lesson
    val t = vm.teaching
    val scroll = remember(vm.lessonIndex) { ScrollState(0) }
    val hl = remember { GoHighlighter(vm.course.ui.keywords, vm.course.ui.types, vm.course.ui.funcs) }
    val concept = remember(vm.lessonIndex) { t.conceptText(l) }
    val crumb = remember(vm.lessonIndex) { t.breadcrumb(l) }
    val diff = remember(vm.lessonIndex) { t.difficulty(l) }

    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (crumb != null) Text(crumb, color = C.MUTED, fontSize = 11.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(diff, color = C.ACCENT, fontSize = 11.sp, modifier = Modifier.weight(1f))
            TextButton(onClick = { vm.bumpFont(-1) }) { Text("A−", color = C.MUTED) }
            TextButton(onClick = { vm.bumpFont(1) }) { Text("A+", color = C.MUTED) }
            TextButton(onClick = { vm.resetFont() }) { Text("Reset", color = C.MUTED, fontSize = 12.sp) }
        }
        ConceptText(concept, vm.fontSize)

        if (l.hasEditor) {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("CODE")
                Spacer(Modifier.width(8.dp))
                Text(
                    when {
                        l.isChallenge && !vm.solutionShown -> "write it yourself — Show solution if stuck"
                        l.isChallenge -> "solution loaded"
                        else -> "run it, then change it and run again"
                    },
                    color = C.MUTED, fontSize = 11.sp,
                )
            }
            CodeEditor(value = vm.code, onValueChange = { vm.code = it }, highlighter = hl)
            SymbolBar { k -> vm.code = vm.code.insert(k) }
            val typos = remember(vm.code.text) { t.typoHints(vm.code.text) }
            if (typos.isNotBlank()) Text(typos, color = C.ACCENT2, fontSize = 12.sp)

            OutlinedTextField(
                value = vm.stdin, onValueChange = { vm.stdin = it },
                label = { Text("stdin (typed input for the program)") },
                textStyle = codeStyle, colors = fieldColors(), modifier = Modifier.fillMaxWidth(), maxLines = 4,
            )
            OutlinedTextField(
                value = vm.predict, onValueChange = { vm.predict = it },
                label = { Text("predict: what will it print? (optional — fill in before Run)") },
                textStyle = codeStyle, colors = fieldColors(), modifier = Modifier.fillMaxWidth(), maxLines = 4,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                AccentButton(if (vm.running) "Running…" else "  Run  ", enabled = !vm.running) { vm.run() }
                GhostButton("Reset") { vm.resetCode() }
                if (l.isChallenge) GhostButton("Show solution", enabled = !vm.solutionShown) { vm.showSolution() }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("OUTPUT")
                Spacer(Modifier.width(8.dp))
                Text(vm.outputStatus, color = outColor(vm.outputKind), fontSize = 11.sp)
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp)
                    .background(C.CODEBG, RoundedCornerShape(8.dp))
                    .border(1.dp, C.LINE, RoundedCornerShape(8.dp))
                    .horizontalScroll(rememberScrollState())
                    .padding(10.dp)
            ) {
                SelectionContainer {
                    Text(
                        vm.output.ifEmpty { "Press Run to build and run this program on your phone." },
                        style = codeStyle.copy(color = if (vm.output.isEmpty()) C.MUTED else outColor(vm.outputKind)),
                    )
                }
            }
        } else {
            Text(
                "This is a concept lesson — there's no code to run. Read the explanation, then take the Quiz or press 'Mark complete'.",
                color = C.MUTED, fontSize = 12.sp,
            )
        }

        if (l.title in vm.completed && l.index < vm.course.lessons.lastIndex) {
            AccentButton("Next lesson ▸", color = C.OK, modifier = Modifier.fillMaxWidth()) { vm.next() }
        }
        if (l.anatomy && (l.branchParent != null || l.branchChildren.isNotEmpty())) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (l.branchParent != null) GhostButton("◂ Back up") { vm.backUp() }
                if (l.branchChildren.isNotEmpty()) GhostButton("Go deeper ▸") { vm.goDeeper() }
            }
        }

        Spacer(Modifier.height(6.dp))
        val prompts = vm.course.ui.selfExplain
        val prompt = if (prompts.isNotEmpty()) prompts[l.index % prompts.size] else ""
        Row {
            SectionLabel("EXPLAIN IT BACK")
            Text(" — $prompt", color = C.MUTED, fontSize = 11.sp)
        }
        OutlinedTextField(
            value = vm.note, onValueChange = { vm.saveNote(it) },
            placeholder = { Text("Your own words. Saved automatically, one note per lesson.", color = C.MUTED) },
            colors = fieldColors(), modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp),
        )
        Spacer(Modifier.height(24.dp))
    }
}

fun outColor(kind: String): Color = when (kind) {
    "ok" -> C.OK
    "error" -> C.ERR
    "warn" -> C.ACCENT2
    else -> C.MUTED
}

// ─────────────────────────────────────────────────────────────────── Quiz
@Composable
fun OptionCard(text: String, bg: Color, fg: Color, border: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .background(bg, RoundedCornerShape(8.dp))
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) { Text(text, color = fg, fontSize = 14.sp) }
}

@Composable
fun QuizTab(vm: AppViewModel) {
    val q = vm.lesson.quiz
    var choice by remember(vm.lessonIndex) { mutableIntStateOf(-1) }
    var checked by remember(vm.lessonIndex) { mutableIntStateOf(-1) }   // the choice that was checked
    var feedback by remember(vm.lessonIndex) { mutableStateOf("") }
    var fbColor by remember(vm.lessonIndex) { mutableStateOf(C.MUTED) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SectionLabel("QUIZ")
        Text(q.q, color = C.FG, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        q.options.forEachIndexed { i, opt ->
            val right = checked >= 0 && checked == q.answer
            val wrong = checked >= 0 && checked != q.answer
            val (bg, fg, bd) = when {
                right && i == q.answer -> Triple(C.OKBG, C.OK, C.OK)
                right -> Triple(C.PANEL, C.MUTED, C.LINE)
                wrong && i == checked -> Triple(C.ERRBG, C.ERR, C.ERR)
                i == choice -> Triple(C.SELBG, C.FG, C.ACCENT)
                else -> Triple(C.PANEL, C.FG, C.LINE)
            }
            OptionCard(opt, bg, fg, bd) {
                choice = i
                if (checked >= 0 && checked != q.answer) { checked = -1 }   // retry after a miss
            }
        }
        Spacer(Modifier.height(4.dp))
        AccentButton("Check answer") {
            if (choice < 0) {
                feedback = "Select an answer first."; fbColor = C.MUTED
            } else {
                checked = choice
                if (choice == q.answer) {
                    feedback = "✓  Correct!  ${q.explain}   (press 'Mark complete' when you're ready)"
                    fbColor = C.OK
                } else {
                    feedback = "✗  Not quite.  " + (q.hint ?: "the answer is explained in the concept above — re-read it, then try again.")
                    fbColor = C.ACCENT2
                }
            }
        }
        if (feedback.isNotEmpty()) Text(feedback, color = fbColor, fontSize = 14.sp)
    }
}

// ──────────────────────────────────────────────────────────────── Parsons
@Composable
fun ParsonsTab(vm: AppViewModel) {
    val correct = remember(vm.lessonIndex) { vm.teaching.parsonsLines(vm.lesson) }
    if (correct == null) {
        Column(Modifier.fillMaxSize().padding(14.dp)) {
            SectionLabel("PARSONS PROBLEM")
            Spacer(Modifier.height(8.dp))
            Text(
                "This lesson doesn't have a code sample suited to a Parsons problem. Try one of the lessons with a " +
                    "runnable program — open it, then come back to this tab.",
                color = C.MUTED, fontSize = 14.sp,
            )
        }
        return
    }
    fun scramble(): List<String> {
        var s = correct.shuffled()
        repeat(6) { if (s == correct) s = correct.shuffled() }
        return s
    }
    var order by remember(vm.lessonIndex) { mutableStateOf(scramble()) }
    var sel by remember(vm.lessonIndex) { mutableIntStateOf(-1) }
    var status by remember(vm.lessonIndex) { mutableStateOf("${correct.size} lines") }
    var statusColor by remember(vm.lessonIndex) { mutableStateOf(C.MUTED) }
    var feedback by remember(vm.lessonIndex) {
        mutableStateOf("Tap a line to select it, then use ↑/↓ to move it. When you think the order is right, press 'Check order'.")
    }
    var fbColor by remember(vm.lessonIndex) { mutableStateOf(C.MUTED) }

    fun move(d: Int) {
        val i = sel
        val j = i + d
        if (i < 0 || j < 0 || j >= order.size) return
        val m = order.toMutableList()
        val x = m.removeAt(i)
        m.add(j, x)
        order = m
        sel = j
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionLabel("PARSONS PROBLEM", Modifier.weight(1f))
            Text(status, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text("the lines are correct but scrambled — put them in order", color = C.MUTED, fontSize = 11.sp)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            GhostButton("↑ Up") { move(-1) }
            GhostButton("↓ Down") { move(1) }
            GhostButton("Shuffle") {
                order = correct.shuffled(); sel = -1
                feedback = "Reshuffled."; fbColor = C.MUTED
            }
            AccentButton("Check") {
                if (order == correct) {
                    status = "solved ✓"; statusColor = C.OK
                    feedback = "✓  Correct order! That's exactly how the program is written. Switch to the Lesson tab and " +
                        "press Run to see it execute — then try writing it yourself from a blank editor."
                    fbColor = C.OK
                } else {
                    val right = order.indices.count { order[it] == correct[it] }
                    status = "$right / ${correct.size} in place"; statusColor = C.ACCENT2
                    feedback = "Not quite — $right of ${correct.size} lines are in the right spot. Think about what has to " +
                        "happen first: the package clause at the top, then the import block, then types and helper funcs, " +
                        "then main() with its statements in the order they must run, and each closing brace after its body."
                    fbColor = C.ACCENT2
                }
            }
        }
        Text(feedback, color = fbColor, fontSize = 13.sp, modifier = Modifier.padding(vertical = 6.dp))
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(C.CODEBG, RoundedCornerShape(8.dp))
                .border(1.dp, C.LINE, RoundedCornerShape(8.dp))
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
                .padding(6.dp)
        ) {
            order.forEachIndexed { i, line ->
                Text(
                    line.ifEmpty { " " }, style = codeStyle,
                    modifier = Modifier
                        .background(if (i == sel) C.SELBG else C.CODEBG)
                        .clickable { sel = i }
                        .padding(horizontal = 6.dp, vertical = 7.dp),
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────── Check
@Composable
fun CheckTab(vm: AppViewModel) {
    val findings = vm.checkFindings
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionLabel("CHECK")
        Text("a dated-practice sweep of the code in the Lesson editor, plus go vet", color = C.MUTED, fontSize = 11.sp)
        AccentButton(if (vm.checkRunning) "Checking…" else "Check the editor's code", enabled = !vm.checkRunning) { vm.runCheck() }
        val code = vm.code.text
        when {
            findings == null -> Text(
                "Write or paste code in the Lesson tab's editor, then press 'Check the editor's code'. It looks for " +
                    "deprecated APIs, missing timeouts, WaitGroup and mutex misuse, ignored errors and more, then runs go vet.",
                color = C.MUTED, fontSize = 13.sp,
            )
            code.isBlank() -> Text(
                "The editor is empty. Open a lesson with code (or paste your own) and press Check again.",
                color = C.MUTED, fontSize = 13.sp,
            )
            else -> {
                Text("main.go  (${code.split("\n").size} lines)", color = C.FG, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (findings.isEmpty()) Text("clean under the sweep", color = C.OK, fontSize = 13.sp)
                findings.forEach { f ->
                    val sevColor = when (f.sev) {
                        "BLOCKING" -> C.ERR
                        "MAJOR" -> C.ACCENT2
                        "MINOR" -> C.ACCENT
                        else -> C.MUTED
                    }
                    Panel {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${f.sev}  line ${f.line}", color = sevColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(f.src.take(70), style = codeStyle.copy(color = C.MUTED, fontSize = 12.sp))
                            Text(f.why, color = C.FG, fontSize = 13.sp)
                            Text("→ ${f.fix}", color = C.OK, fontSize = 13.sp)
                            val sg = f.subgoals?.let { vm.course.subgoals[it] }
                            if (sg != null) {
                                Text("subgoals for this pattern:", color = C.ACCENT, fontSize = 12.sp)
                                sg.forEachIndexed { i, s -> Text("  ${i + 1}. $s", color = C.FG, fontSize = 12.sp) }
                            }
                        }
                    }
                }
                val vet = vm.vetOutput
                when {
                    vm.checkRunning -> Text("running go vet…", color = C.MUTED, fontSize = 13.sp)
                    vet == null -> {}
                    vet.startsWith("(go vet unavailable") -> Text(vet, color = C.MUTED, fontSize = 13.sp)
                    vet.isNotEmpty() -> {
                        Text("go vet says:", color = C.ACCENT2, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(vet, style = codeStyle.copy(fontSize = 12.sp), modifier = Modifier.horizontalScroll(rememberScrollState()))
                    }
                    else -> Text("go vet: clean", color = C.OK, fontSize = 13.sp)
                }
                val total = findings.size + if (!vet.isNullOrEmpty() && !vet.startsWith("(go vet unavailable")) 1 else 0
                Text("$total finding(s)", color = C.FG, fontSize = 13.sp)
                Text(
                    "Heuristic sweep. Clean here is necessary, not sufficient - also run: gofmt -l . && go vet ./... && " +
                        "go test -race ./... && staticcheck ./...",
                    color = C.MUTED, fontSize = 11.sp,
                )
            }
        }
    }
}

// ===== PracticeScreens.kt =====
/** Common page frame: header with back arrow + scrolling body. */
@Composable
fun Page(vm: AppViewModel, title: String, scroll: Boolean = true, body: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(C.BG)) {
        Row(
            Modifier.fillMaxWidth().background(C.PANEL).padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { if (!vm.back()) vm.open(Screen.Home) }) { Text("←", color = C.FG, fontSize = 20.sp) }
            Text(title, color = C.FG, fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        val m = if (scroll) Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(14.dp)
        else Modifier.fillMaxSize().padding(14.dp)
        Column(m, verticalArrangement = Arrangement.spacedBy(8.dp), content = body)
    }
}

@Composable
fun MonoBlock(text: String, color: Color = C.FG, size: Int = 12) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(C.CODEBG, RoundedCornerShape(8.dp))
            .border(1.dp, C.LINE, RoundedCornerShape(8.dp))
            .horizontalScroll(rememberScrollState())
            .padding(10.dp)
    ) {
        SelectionContainer { Text(text, style = codeStyle.copy(color = color, fontSize = size.sp, lineHeight = (size * 1.45f).sp)) }
    }
}

// ═══════════════════════════════════════════════════════════════ Review
@Composable
fun ReviewScreen(vm: AppViewModel) {
    val leitner = vm.leitner
    var deck by remember { mutableStateOf<List<Pair<String, Quiz>>>(emptyList()) }
    var pos by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var dueTotal by remember { mutableIntStateOf(0) }
    var scope by remember { mutableStateOf("") }
    var choice by remember { mutableIntStateOf(-1) }
    var answered by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf("") }
    var fbColor by remember { mutableStateOf(C.MUTED) }
    var started by remember { mutableStateOf(false) }

    fun loadCard() {
        choice = -1
        answered = false
        if (pos < deck.size) {
            val (title, _) = deck[pos]
            val rec = leitner.record(title)
            val newness = if (rec == null) "new card" else "box ${rec.optInt("box", 1)}/${Leitner.MAX_BOX}"
            feedback = "From: $title   ·   $scope   ·   $newness"
            fbColor = C.MUTED
        }
    }

    fun start() {
        val done = vm.course.lessons.filter { it.title in vm.completed }
        val pool = if (done.size >= 5) {
            scope = "from your completed lessons"; done
        } else {
            scope = "from all lessons — complete some to focus this on them"; vm.course.lessons
        }
        val due = pool.filter { leitner.isDue(it.title) }.shuffled()
            .sortedBy { leitner.record(it.title)?.optInt("box", 1) ?: 1 }
        val resting = pool.filter { !leitner.isDue(it.title) }.shuffled()
        dueTotal = due.size
        deck = (due + resting).map { it.title to it.quiz }.take(20)
        pos = 0
        score = 0
        loadCard()
    }

    androidx.compose.runtime.LaunchedEffect(Unit) { start(); started = true }

    Page(vm, "Review — spaced repetition") {
        if (!started) return@Page
        if (pos >= deck.size) {
            val total = deck.size
            val (learning, mid, known) = leitner.summary()
            Text(
                if (total == 0) "Nothing due right now — you're caught up! ★" else "Session complete — you scored $score / $total.",
                color = C.FG, fontSize = 17.sp, fontWeight = FontWeight.Bold,
            )
            val pct = if (total > 0) Math.round(100.0 * score / total).toInt() else 0
            Text(
                if (total == 0) "Spaced repetition schedules cards by how well you know them. Come back later and the ones " +
                    "you're about to forget will resurface. Or press 'New deck' to drill anyway.\n\nYour cards — learning: " +
                    "$learning   ·   review: $mid   ·   well-known: $known"
                else "That's $pct%. Cards you missed will come back soon; cards you nailed move to a longer interval.\n\n" +
                    "Your cards — learning: $learning   ·   review: $mid   ·   well-known: $known",
                color = if (total == 0) C.MUTED else if (pct >= 70) C.OK else C.ACCENT2, fontSize = 14.sp,
            )
            Text("done  ·  $score / $total", color = C.MUTED, fontSize = 12.sp)
            AccentButton("New deck") { start() }
            return@Page
        }
        val (title, q) = deck[pos]
        Text(
            "card ${pos + 1} / ${deck.size}  ·  score $score" + (if (dueTotal > 0) "  ·  $dueTotal due" else ""),
            color = C.MUTED, fontSize = 12.sp,
        )
        Text(q.q, color = C.FG, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        q.options.forEachIndexed { i, opt ->
            val (bg, fg, bd) = when {
                answered && i == q.answer -> Triple(C.OKBG, C.OK, C.OK)
                answered && i == choice -> Triple(C.ERRBG, C.ERR, C.ERR)
                answered -> Triple(C.PANEL, C.MUTED, C.LINE)
                i == choice -> Triple(C.SELBG, C.FG, C.ACCENT)
                else -> Triple(C.PANEL, C.FG, C.LINE)
            }
            OptionCard(opt, bg, fg, bd) { if (!answered) choice = i }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!answered) {
                AccentButton("Check") {
                    if (choice < 0) {
                        feedback = "Select an answer first."; fbColor = C.MUTED
                    } else {
                        answered = true
                        val got = choice == q.answer
                        leitner.update(title, got)
                        val box = leitner.record(title)?.optInt("box", 1) ?: 1
                        if (got) {
                            score++
                            feedback = "✓  Correct!  ${q.explain}\n(promoted to box $box — next review in ${leitner.intervalPhrase(box)})"
                            fbColor = C.OK
                        } else {
                            feedback = "✗  Not quite.  ${q.explain}\n(back to box 1 — you'll see this again soon)"
                            fbColor = C.ACCENT2
                        }
                    }
                }
            } else {
                AccentButton("Next card ▸") { pos++; loadCard() }
            }
            GhostButton("New deck") { start() }
        }
        Text(feedback, color = fbColor, fontSize = 14.sp)
    }
}

// ═══════════════════════════════════════════════════════════════ Drill
private fun norm(s: String) = s.trim().lowercase().replace(Regex("\\s+"), " ")

@Composable
fun DrillScreen(vm: AppViewModel) {
    val sched = vm.drill
    var mode by remember { mutableStateOf("menu") }        // menu, session, done
    var limit by remember { mutableIntStateOf(10) }
    var queue by remember { mutableStateOf<List<DrillItem>>(emptyList()) }
    var relearn by remember { mutableStateOf<List<DrillItem>>(emptyList()) }
    var sessionSize by remember { mutableIntStateOf(0) }
    var done by remember { mutableIntStateOf(0) }
    var current by remember { mutableStateOf<DrillItem?>(null) }
    var revealed by remember { mutableStateOf(false) }
    var lastMsg by remember { mutableStateOf("") }
    var relearnBanner by remember { mutableStateOf(false) }
    var mixed by remember { mutableStateOf("") }
    var poolSize by remember { mutableIntStateOf(0) }
    var confirmReset by remember { mutableStateOf(false) }

    fun advance() {
        revealed = false
        var q = queue
        if (q.isEmpty() && relearn.isNotEmpty()) {
            q = relearn
            relearn = emptyList()
            sessionSize += q.size
            relearnBanner = true
        }
        if (q.isEmpty()) {
            current = null
            sched.finishSession()
            mode = "done"
            return
        }
        current = q.first()
        queue = q.drop(1)
        done++
    }

    fun startSession() {
        val pool = sched.dueItems()
        poolSize = pool.size
        val session = sched.interleave(pool).take(limit)
        queue = session
        relearn = emptyList()
        sessionSize = session.size
        done = 0
        lastMsg = ""
        relearnBanner = false
        mixed = session.map { it.concept }.distinct().joinToString(", ")
        mode = "session"
        advance()
    }

    fun grade(g: Int?) {
        val cur = current ?: return
        if (g == null) {
            lastMsg = "skipped"
        } else {
            val iv = sched.reschedule(cur.id, g)
            if (g == 0) {
                relearn = relearn + cur
                lastMsg = "→ back later this session"
            } else {
                lastMsg = "→ next in $iv day(s)"
            }
        }
        advance()
    }

    Page(vm, "Drill — interleaved spaced practice") {
        when (mode) {
            "menu" -> {
                val due = sched.dueItems().size
                Text("${vm.course.drillItems.size} practice items · $due due · ${sched.sessions} session(s) run", color = C.MUTED, fontSize = 12.sp)
                Text(
                    "Mixed sets feel harder than practising one topic at a time. That difficulty is deliberate and it is why " +
                        "the material sticks. Blocked practice feels more productive and retains worse.",
                    color = C.FG, fontSize = 14.sp,
                )
                if (due == 0) {
                    Text("nothing due.", color = C.OK, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    sched.nextDue()?.let { Text("next item due $it", color = C.MUTED, fontSize = 13.sp) }
                    Text("Spacing means the gap is the point. Come back then.", color = C.MUTED, fontSize = 13.sp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("items per session: ", color = C.FG, fontSize = 14.sp)
                        GhostButton("−") { limit = (limit - 5).coerceAtLeast(5) }
                        Text("  $limit  ", color = C.ACCENT2, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        GhostButton("+") { limit = (limit + 5).coerceAtMost(100) }
                    }
                    AccentButton("Start session ${sched.sessions + 1}", modifier = Modifier.fillMaxWidth()) { startSession() }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GhostButton("Retention report") { vm.open(Screen.Report) }
                    GhostButton("Reset drill progress") { confirmReset = true }
                }
            }
            "done" -> {
                Text("done. $done items.", color = C.OK, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                if (lastMsg.isNotEmpty()) Text(lastMsg, color = C.MUTED, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccentButton("Back to drill menu") { mode = "menu" }
                    GhostButton("Retention report") { vm.open(Screen.Report) }
                }
            }
            else -> {
                val item = current
                if (item == null) {
                    AccentButton("Back to drill menu") { mode = "menu" }
                    return@Page
                }
                Text("mixed: $mixed", color = C.MUTED, fontSize = 11.sp)
                if (relearnBanner) Text("relearn queue", color = C.ACCENT2, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("$done/$sessionSize  ·  ${item.concept}  ·  module ${item.module}   ($poolSize due)", color = C.MUTED, fontSize = 12.sp)
                if (lastMsg.isNotEmpty()) Text(lastMsg, color = C.MUTED, fontSize = 11.sp)
                // `key` resets per-item local state (inputs, shuffles) for every new card
                androidx.compose.runtime.key(item.id, done) {
                    DrillCard(vm, item, revealed) { revealed = true }
                }
                if (revealed) {
                    Text("[1] missed   [2] hard   [3] got it   [4] easy   [s] skip", color = C.MUTED, fontSize = 12.sp)
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        AccentButton("1 missed", color = C.ERR) { grade(0) }
                        AccentButton("2 hard", color = C.ACCENT2) { grade(1) }
                        AccentButton("3 got it", color = C.ACCENT) { grade(2) }
                        AccentButton("4 easy", color = C.OK) { grade(3) }
                        GhostButton("skip") { grade(null) }
                    }
                } else {
                    GhostButton("skip") { grade(null) }
                }
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            containerColor = C.PANEL,
            title = { Text("Reset drill progress?", color = C.FG) },
            text = { Text("This deletes every drill item's schedule and the session count. Lessons, reviews and XP are kept.", color = C.FG) },
            confirmButton = { TextButton(onClick = { sched.reset(); confirmReset = false; mode = "menu" }) { Text("Delete", color = C.ERR) } },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Keep") } },
        )
    }
}

@Composable
fun Subgoals(vm: AppViewModel, key: String?) {
    val sg = key?.let { vm.course.subgoals[it] } ?: return
    Text("subgoals for this pattern:", color = C.ACCENT, fontSize = 13.sp)
    sg.forEachIndexed { i, s -> Text("  ${i + 1}. $s", color = C.FG, fontSize = 13.sp) }
}

@Composable
fun DiagramFor(vm: AppViewModel, key: String?) {
    val d = vm.course.diagram(key) ?: return
    Text("memory:", color = C.MUTED, fontSize = 12.sp)
    MonoBlock(vm.teaching.renderMemory(d.rows, d.title), size = 11)
}

@Composable
fun DrillCard(vm: AppViewModel, item: DrillItem, revealed: Boolean, onReveal: () -> Unit) {
    when (item.kind) {
        "predict" -> {
            var got by remember { mutableStateOf("") }
            Text("PREDICT the output. Write it down before you run it.", color = C.ACCENT, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            MonoBlock(item.code ?: "", color = C.MUTED)
            OutlinedTextField(
                value = got, onValueChange = { if (!revealed) got = it }, label = { Text("what does it print?") },
                textStyle = codeStyle, colors = fieldColors(), modifier = Modifier.fillMaxWidth(), minLines = 2,
            )
            if (!revealed) {
                AccentButton("Check") { onReveal() }
            } else {
                val correct = norm(got) == norm(item.answer ?: "")
                if (correct) Text("correct", color = C.OK, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                else {
                    Text("not what it prints. Actual output:", color = C.ERR, fontSize = 14.sp)
                    MonoBlock(item.answer ?: "", color = C.FG)
                }
                Text(item.why ?: "", color = C.FG, fontSize = 14.sp)
                DiagramFor(vm, item.diagram)
                if (!correct) Text("Grade yourself - formatting differences do not count as wrong.", color = C.MUTED, fontSize = 12.sp)
            }
        }
        "parsons" -> {
            val display = remember {
                (item.blocks.map { Triple(it.key, it.value, false) } +
                    item.distractors.map { Triple(it.key, it.value.text, true) }).shuffled()
            }
            var chosen by remember { mutableStateOf<List<String>>(emptyList()) }
            Text("PARSONS - order the blocks. Some do not belong.", color = C.ACCENT, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(item.prompt ?: "", color = C.FG, fontSize = 14.sp)
            Text("Tap blocks in the order they should run. Tap a placed block to remove it.", color = C.MUTED, fontSize = 12.sp)
            display.forEach { (k, text, _) ->
                val used = k in chosen
                Text(
                    text, style = codeStyle.copy(color = if (used) C.LINE else C.FG, fontSize = 12.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .background(C.CODEBG, RoundedCornerShape(6.dp))
                        .border(1.dp, C.LINE, RoundedCornerShape(6.dp))
                        .clickable(enabled = !used && !revealed) { chosen = chosen + k }
                        .padding(8.dp),
                )
            }
            SectionLabel("YOUR ORDER")
            if (chosen.isEmpty()) Text("(nothing yet)", color = C.MUTED, fontSize = 12.sp)
            chosen.forEachIndexed { i, k ->
                val text = item.blocks[k] ?: item.distractors[k]?.text ?: k
                Text(
                    "${i + 1}. $text", style = codeStyle.copy(fontSize = 12.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(C.SELBG, RoundedCornerShape(6.dp))
                        .clickable(enabled = !revealed) { chosen = chosen.filterIndexed { j, _ -> j != i } }
                        .padding(8.dp),
                )
            }
            if (!revealed) {
                AccentButton("Check order") { onReveal() }
            } else {
                if (chosen == item.solution) Text("correct order", color = C.OK, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                else {
                    Text("not the right sequence.", color = C.ERR, fontSize = 14.sp)
                    chosen.filter { it in item.distractors }.forEach { d ->
                        val dd = item.distractors.getValue(d)
                        Text("you used: ${dd.text}", color = C.ACCENT2, fontSize = 13.sp)
                        Text(dd.why, color = C.FG, fontSize = 13.sp)
                    }
                    Text("correct:", color = C.MUTED, fontSize = 12.sp)
                    MonoBlock(item.solution.joinToString("\n") { item.blocks[it] ?: it })
                }
                Subgoals(vm, item.subgoals)
            }
        }
        "faded" -> {
            val n = item.blanks.size
            var got by remember { mutableStateOf(List(n) { "" }) }
            Text("FADED - fill each blank.", color = C.ACCENT, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(item.prompt ?: "", color = C.FG, fontSize = 14.sp)
            MonoBlock(item.code ?: "", color = C.MUTED)
            for (i in 0 until n) {
                OutlinedTextField(
                    value = got[i], onValueChange = { v -> if (!revealed) got = got.toMutableList().also { it[i] = v } },
                    label = { Text("blank ${i + 1}") }, singleLine = true,
                    textStyle = codeStyle, colors = fieldColors(), modifier = Modifier.fillMaxWidth(),
                )
            }
            if (!revealed) {
                AccentButton("Check") { onReveal() }
            } else {
                val ok = item.blanks.indices.all { i -> norm(got[i]) in item.blanks[i].map { norm(it) } }
                if (ok) Text("correct", color = C.OK, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                else {
                    Text("not quite. Answers:", color = C.ERR, fontSize = 14.sp)
                    item.blanks.forEachIndexed { i, acc ->
                        Text("blank ${i + 1}: ${acc.firstOrNull() ?: ""}", style = codeStyle.copy(fontWeight = FontWeight.Bold))
                    }
                }
                Text(item.why ?: "", color = C.FG, fontSize = 14.sp)
                Subgoals(vm, item.subgoals)
                DiagramFor(vm, item.diagram)
            }
        }
        "trace" -> {
            Text("TRACE the diagram.", color = C.ACCENT, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            MonoBlock(vm.teaching.renderMemory(item.rows, null), size = 11)
            Text(item.question ?: "", color = C.FG, fontSize = 14.sp)
            if (!revealed) AccentButton("Think it through, then show the answer") { onReveal() }
            else Text(item.answer ?: "", color = C.FG, fontSize = 14.sp)
        }
        else -> { // recall
            Text("RECALL - closed book.", color = C.ACCENT, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(item.prompt ?: "", color = C.FG, fontSize = 15.sp)
            if (!revealed) AccentButton("Answer out loud or on paper, then show the answer") { onReveal() }
            else {
                Text(item.answer ?: "", color = C.FG, fontSize = 14.sp)
                Subgoals(vm, item.subgoals)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════ Report
@Composable
fun ReportScreen(vm: AppViewModel) {
    val rows = remember { vm.drill.report() }
    Page(vm, "Retention map") {
        Text("T = taught (module)   R = retrieved   gap = taught, never retrieved", color = C.MUTED, fontSize = 11.sp)
        Column(Modifier.horizontalScroll(rememberScrollState())) {
            Text(
                "concept".padEnd(24) + " " + "mod".padStart(3) + " " + "items".padStart(5) + " " + "seen".padStart(5) + " " +
                    "lapses".padStart(6) + "  last        status",
                style = codeStyle.copy(color = C.MUTED, fontSize = 11.sp),
            )
            Text("─".repeat(84), style = codeStyle.copy(color = C.LINE, fontSize = 11.sp))
            rows.forEach { r ->
                val color = when (r.kind) { "gap" -> C.ERR; "warm" -> C.OK; else -> C.ACCENT2 }
                Row {
                    Text(
                        r.concept.padEnd(24) + " " + r.module.toString().padStart(3) + " " + r.items.toString().padStart(5) + " " +
                            r.seen.toString().padStart(5) + " " + r.lapses.toString().padStart(6) + "  " + (r.last ?: "-").padEnd(11) + " ",
                        style = codeStyle.copy(fontSize = 11.sp),
                    )
                    Text(r.status, style = codeStyle.copy(color = color, fontSize = 11.sp))
                }
            }
        }
        val gaps = rows.filter { it.kind == "gap" }.map { it.concept }
        val overdue = rows.filter { it.kind == "due" }.map { it.concept }
        if (gaps.isNotEmpty()) {
            Text("retention gaps: " + gaps.joinToString(", "), color = C.ERR, fontSize = 13.sp)
            Text(
                "A concept taught once and never deliberately retrieved is the standard failure mode - it does not announce " +
                    "itself until the capstone. These rows are the ones to schedule.",
                color = C.MUTED, fontSize = 12.sp,
            )
        } else {
            Text("no untouched concepts", color = C.OK, fontSize = 13.sp)
        }
        if (overdue.isNotEmpty()) Text("due now: " + overdue.joinToString(", "), color = C.ACCENT2, fontSize = 13.sp)
        Text("sessions run: ${vm.drill.sessions}", color = C.MUTED, fontSize = 12.sp)
        AccentButton("Start a drill") { vm.open(Screen.Drill) }
    }
}

// ═══════════════════════════════════════════════════════════════ Diagrams
@Composable
fun DiagramsScreen(vm: AppViewModel) {
    Page(vm, "Memory diagrams") {
        vm.course.diagrams.forEach { d ->
            Text("[${d.key}]", color = C.ACCENT, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            MonoBlock(vm.teaching.renderMemory(d.rows, d.title), size = 11)
            Spacer(Modifier.height(4.dp))
        }
        Text("Convention: addresses are small fake numbers on purpose.", color = C.MUTED, fontSize = 12.sp)
        Text("Your machine prints something else. The topology is the lesson.", color = C.MUTED, fontSize = 12.sp)
    }
}

// ═══════════════════════════════════════════════════════════════ Stats
@Composable
fun StatsScreen(vm: AppViewModel) {
    val e = vm.engage
    var goal by remember { mutableIntStateOf(e.goal) }
    Page(vm, "Stats") {
        val (rank, next) = e.rank(e.xp)
        Text(vm.engageSummary, color = C.ACCENT2, fontSize = 13.sp)
        Panel {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("★ ${e.xp} XP  ·  $rank", color = C.FG, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (next != null) Text("${next.first - e.xp} XP to ${next.second}", color = C.MUTED, fontSize = 13.sp)
                else Text("Top rank reached.", color = C.OK, fontSize = 13.sp)
                Text("♨ ${e.streakAlive()}-day streak  ·  best ${e.bestStreak}", color = C.FG, fontSize = 14.sp)
                Text("streak freezes: ${e.freezes}  ·  lucky gems: ${e.gems}", color = C.FG, fontSize = 14.sp)
                Text("today: ${e.todayCount} / ${e.goal} lessons", color = C.FG, fontSize = 14.sp)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Daily goal: ", color = C.FG, fontSize = 14.sp)
            GhostButton("−") { goal = (goal - 1).coerceAtLeast(1); vm.setGoal(goal) }
            Text("  $goal  ", color = C.ACCENT2, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            GhostButton("+") { goal = (goal + 1).coerceAtMost(50); vm.setGoal(goal) }
            Text("  lessons/day", color = C.MUTED, fontSize = 13.sp)
        }
        val n = vm.course.lessons.size
        Text("Lessons: ${vm.doneCount} / $n complete", color = C.FG, fontSize = 14.sp)
        val (learning, mid, known) = vm.leitner.summary()
        Text("Review cards — learning: $learning  ·  review: $mid  ·  well-known: $known", color = C.FG, fontSize = 14.sp)
        Text("Drill sessions run: ${vm.drill.sessions}", color = C.FG, fontSize = 14.sp)
        SectionLabel("BY MODULE")
        vm.course.modules.forEachIndexed { m, name ->
            val ls = vm.course.lessons.filter { it.module == m }
            if (ls.isEmpty()) return@forEachIndexed
            val d = ls.count { it.title in vm.completed }
            Text("${if (d == ls.size) "✓" else "·"}  $name — $d/${ls.size}", color = if (d == ls.size) C.OK else C.FG, fontSize = 13.sp)
        }
    }
}

// ═══════════════════════════════════════════════════════════════ Go terminal
@Composable
fun PlaygroundScreen(vm: AppViewModel) {
    val hl = remember { GoHighlighter(vm.course.ui.keywords, vm.course.ui.types, vm.course.ui.funcs) }
    Page(vm, "Go terminal") {
        InstallBanner(vm)
        Text(
            "A scratch module (main.go + go.mod) on your phone. Type any go command: run . · build . · vet . · test · " +
                "version · env · doc fmt.Println · list std",
            color = C.MUTED, fontSize = 12.sp,
        )
        CodeEditor(value = vm.playCode, onValueChange = { vm.playCode = it }, highlighter = hl, minHeight = 220)
        SymbolBar { k -> vm.playCode = vm.playCode.insert(k) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("go ", style = codeStyle.copy(color = C.ACCENT, fontSize = 15.sp))
            OutlinedTextField(
                value = vm.playArgs, onValueChange = { vm.playArgs = it }, singleLine = true,
                textStyle = codeStyle, colors = fieldColors(), modifier = Modifier.weight(1f),
            )
        }
        OutlinedTextField(
            value = vm.playStdin, onValueChange = { vm.playStdin = it }, label = { Text("stdin") },
            textStyle = codeStyle, colors = fieldColors(), modifier = Modifier.fillMaxWidth(), maxLines = 4,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AccentButton(if (vm.playRunning) "Running…" else "Run command", enabled = !vm.playRunning) { vm.runPlayground() }
            GhostButton("Reset code") { vm.playCode = androidx.compose.ui.text.input.TextFieldValue(com.gomastery.app.AppViewModel.PLAY_DEFAULT) }
        }
        if (vm.playOut.isNotEmpty()) MonoBlock(vm.playOut)
    }
}

// ═══════════════════════════════════════════════════════════════ About
@Composable
fun AboutScreen(vm: AppViewModel) {
    Page(vm, "About") {
        Text("Go Mastery", color = C.FG, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "The complete Go course: ${vm.course.lessons.size} lessons across ${vm.course.modules.size} modules, with quizzes, " +
                "Parsons problems, spaced-repetition review, interleaved drills, memory diagrams, the Check sweep with go vet, " +
                "notes, streaks and XP, and the Go Anatomy tree.",
            color = C.FG, fontSize = 14.sp,
        )
        Text(
            "Code is compiled and run by the official Go toolchain (${vm.go.versionText}), bundled inside this app and running " +
                "on the phone itself. No internet connection and no other app is needed.",
            color = C.FG, fontSize = 14.sp,
        )
        InstallBanner(vm)
        SectionLabel("KEYBOARD SHORTCUTS (hardware keyboard)")
        MonoBlock(
            "Ctrl+Enter     Run\nCtrl+→ / Ctrl+←  next / previous lesson\nCtrl+M         mark complete\n" +
                "Ctrl+= / Ctrl+-  bigger / smaller text\nCtrl+0         reset text size\nCtrl+B         look up a word\n" +
                "F7             micro-movie\nF8             short video",
        )
    }
}
