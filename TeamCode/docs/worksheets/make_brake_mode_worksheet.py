"""
Makes the printable worksheet for the brake mode experiment (issue #17).

Run:   python3 make_brake_mode_worksheet.py
Needs: pip install reportlab
Makes: brake-mode-experiment.pdf (in this folder)

Edit the text below and re-run to update the PDF.
"""
import os

from reportlab.lib import colors
from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import inch
from reportlab.platypus import (KeepTogether, PageBreak, Paragraph, SimpleDocTemplate, Spacer, Table,
                                TableStyle)

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "brake-mode-experiment.pdf")

styles = getSampleStyleSheet()
H1 = ParagraphStyle("H1", parent=styles["Title"], fontSize=20, spaceAfter=4)
H2 = ParagraphStyle("H2", parent=styles["Heading2"], fontSize=14, spaceBefore=10, spaceAfter=4,
                    textColor=colors.HexColor("#1F3A68"))
BODY = ParagraphStyle("Body", parent=styles["BodyText"], fontSize=10.5, leading=14)
SMALL = ParagraphStyle("Small", parent=BODY, fontSize=9, leading=11.5, textColor=colors.HexColor("#444444"))
CELL = ParagraphStyle("Cell", parent=BODY, fontSize=9.5, leading=12)
CELLB = ParagraphStyle("CellB", parent=CELL, fontName="Helvetica-Bold")

GRID = colors.HexColor("#888888")
HEAD_BG = colors.HexColor("#DCE6F2")
WRITE_H = 0.42 * inch   # height of rows students write in


def p(text, style=BODY):
    return Paragraph(text, style)


def table(rows, widths, row_height=WRITE_H, header=True):
    """A grid table. Strings become Paragraphs so long text wraps."""
    data = [[c if not isinstance(c, str) else p(c, CELLB if (header and r == 0) else CELL) for c in row]
            for r, row in enumerate(rows)]
    heights = [None] + [row_height] * (len(rows) - 1) if header else [row_height] * len(rows)
    t = Table(data, colWidths=widths, rowHeights=heights)
    style = [
        ("GRID", (0, 0), (-1, -1), 0.6, GRID),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("LEFTPADDING", (0, 0), (-1, -1), 5),
        ("RIGHTPADDING", (0, 0), (-1, -1), 5),
    ]
    if header:
        style.append(("BACKGROUND", (0, 0), (-1, 0), HEAD_BG))
    t.setStyle(TableStyle(style))
    return t


def lines(n, width=7.0 * inch):
    """Blank lines to write on."""
    t = Table([[""]] * n, colWidths=[width], rowHeights=[0.34 * inch] * n)
    t.setStyle(TableStyle([("LINEBELOW", (0, 0), (-1, -1), 0.5, GRID)]))
    return t


def footer(canvas, doc):
    canvas.saveState()
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(colors.HexColor("#666666"))
    canvas.drawString(0.75 * inch, 0.5 * inch, "WMS Irondogs  |  Brake Mode Experiment  |  GitHub issue #17")
    canvas.drawRightString(7.75 * inch, 0.5 * inch, "Page %d" % doc.page)
    canvas.restoreState()


story = []

# ---------------------------------------------------------------- Page 1: background + predictions
story += [
    p("Brake Mode Experiment", H1),
    p("What do our drive motors <b>really</b> do when the driver lets go of the sticks?  (GitHub issue #17)", BODY),
    Spacer(1, 8),
    table([["Team members", "", "Date", ""],
           ["Robot", "", "Battery voltage", ""]],
          [1.3 * inch, 2.6 * inch, 1.3 * inch, 1.8 * inch], row_height=0.36 * inch, header=False),
    Spacer(1, 4),
    p("<b>Equipment:</b> robot + charged battery, Driver Station, tape measure, painter's tape, phone for photos/video. "
      "<b>Safety:</b> clear space at least 10 ft long, people stand to the side, a driver ready to press STOP.", SMALL),

    p("1. What is brake mode?", H2),
    p("<b>Brake mode</b> (in code: <i>ZeroPowerBehavior</i>) decides what a motor does when its power is <b>0</b>, "
      "like when the driver lets go of the sticks. It is a different setting from encoder mode (#11)."),
    Spacer(1, 4),
    table([["Mode", "What the hub does", "What you feel"],
           ["BRAKE", "Connects the motor's wires together. A spinning motor acts like a generator and fights its "
                     "own spinning.", "The robot stops quickly. Hard to push by hand."],
           ["FLOAT", "Disconnects the motor. It spins freely.", "The robot coasts to a stop. Easy to push."]],
          [0.9 * inch, 3.6 * inch, 2.5 * inch], row_height=0.5 * inch),
    Spacer(1, 3),
    p("BRAKE is <b>not</b> \"hold position.\" It uses no power and only resists while the wheels are turning.", SMALL),

    p("2. Our hypothesis (what we think happens)", H2),
    p("Last season we assumed the motors BRAKE by default. But reading the FTC SDK's own code suggests:"),
    p("&nbsp;&nbsp;&nbsp;<b>a.</b> When the robot <b>powers up</b>, the SDK sets all motors to <b>FLOAT</b>."),
    p("&nbsp;&nbsp;&nbsp;<b>b.</b> When a program (OpMode) <b>starts</b>, brake mode is <b>not</b> reset. "
      "The last program's setting <b>carries over</b>."),
    p("&nbsp;&nbsp;&nbsp;<b>c.</b> So <b>manualDriver</b> (which never sets brake mode) would <b>coast</b> after power-up "
      "or after Pinpoint Test, and only brake after a program that sets BRAKE."),
    Spacer(1, 4),
    p("Reading code is not proof. <b>We test it on the real robot.</b>"),

    p("3. Predict FIRST (before you test!)", H2),
    p("Circle your prediction for each step. Scientists write predictions down <b>before</b> the experiment, "
      "so the results can't change what we \"remember\" predicting."),
    Spacer(1, 4),
    table([["Step", "Prediction (circle one)"],
           ["A1: brake mode left over after power-up", "BRAKE     /     FLOAT"],
           ["A2: brake mode left over after Pinpoint Test", "BRAKE     /     FLOAT"],
           ["A3: brake mode left over after Example TeleOp", "BRAKE     /     FLOAT"],
           ["B1: manualDriver after power-up", "STOPS     /     COASTS"],
           ["B2: manualDriver after Pinpoint Test", "STOPS     /     COASTS"],
           ["B3: manualDriver after Example TeleOp", "STOPS     /     COASTS"]],
          [4.2 * inch, 2.8 * inch], row_height=0.3 * inch),
    PageBreak(),
]

# ---------------------------------------------------------------- Page 2: Part A + Part B
story += [
    p("Part A: What brake mode is left over?", H2),
    p("Do the step, then run <b>Brake Mode Check</b> (Tests group) and press <b>INIT</b> only. Write down "
      "\"<b>Brake mode when this OpMode started</b>\". Then press START and STOP.", BODY),
    p("Note: Brake Mode Check itself leaves the motors in BRAKE when it stops. That's why each step starts "
      "with its own setup.", SMALL),
    Spacer(1, 4),
    table([["Step", "Do this first...", "Prediction (from page 1)", "Result: BRAKE or FLOAT"],
           ["A1", "Power the robot OFF, then ON", "", ""],
           ["A2", "Run Pinpoint Test: INIT, START, then STOP", "", ""],
           ["A3", "Run Example TeleOp: INIT, START, then STOP", "", ""],
           ["A4", "Run Pinpoint Test again: INIT, START, then STOP", "", ""]],
          [0.5 * inch, 3.0 * inch, 1.6 * inch, 1.9 * inch]),

    p("Part B: Does it change how manualDriver drives?", H2),
    p("Do the setup, then run <b>manualDriver</b>. Drive forward at <b>full speed</b>, <b>let go</b> at a tape line, "
      "and measure how far past the line the robot rolls. Then, with the robot stopped, try pushing it."),
    Spacer(1, 4),
    table([["Step", "Setup before manualDriver", "Prediction: stops / coasts", "Roll distance (in)",
            "Easy or hard to push?"],
           ["B1", "Power OFF, then ON", "", "", ""],
           ["B2", "Run Pinpoint Test, then STOP", "", "", ""],
           ["B3", "Run Example TeleOp, then STOP", "", "", ""]],
          [0.5 * inch, 2.2 * inch, 1.5 * inch, 1.3 * inch, 1.5 * inch]),
    Spacer(1, 6),
    p("<b>What do you notice?</b> Does the same program (manualDriver) behave differently depending on what ran "
      "before it?"),
    lines(3),
    PageBreak(),
]

# ---------------------------------------------------------------- Page 3: Part C
story += [
    p("Part C: Feel the difference", H2),
    p("Run <b>Brake Mode Check</b> and press START. Press <b>X</b> for BRAKE or <b>Y</b> for FLOAT. "
      "Mark a start line with tape. Drive forward at <b>full speed</b>, <b>let go</b> at the line, "
      "and measure how far past it the robot rolls. Do 3 tries in each mode."),
    Spacer(1, 4),
    table([["Mode", "Try 1 (in)", "Try 2 (in)", "Try 3 (in)", "Average (in)"],
           ["BRAKE (X)", "", "", "", ""],
           ["FLOAT (Y)", "", "", "", ""]],
          [1.4 * inch, 1.4 * inch, 1.4 * inch, 1.4 * inch, 1.4 * inch], row_height=0.5 * inch),
    Spacer(1, 4),
    p("Average = (Try 1 + Try 2 + Try 3) / 3", SMALL),
    Spacer(1, 8),
    table([["Question", "BRAKE", "FLOAT"],
           ["With the robot stopped, how hard is it to push? (1 = easy, 5 = very hard)", "", ""],
           ["Driver 1: which one do you like driving? Why?", "", ""],
           ["Driver 2: which one do you like driving? Why?", "", ""]],
          [3.6 * inch, 1.7 * inch, 1.7 * inch], row_height=0.55 * inch),
    Spacer(1, 8),
    p("<b>Think about the game:</b> the field gets crowded. Which mode helps a driver stop exactly where they want "
      "(lining up to launch, parking in the LOADING ZONE)? Which mode helps when another robot pushes ours?"),
    lines(4),
    PageBreak(),
]

# ---------------------------------------------------------------- Page 4: results + decision
story += [
    p("Results and decision", H2),
    p("<b>Was our hypothesis right?</b>  (circle)     YES     /     NO     /     PARTLY"),
    Spacer(1, 4),
    p("Explain what you found, using your numbers from Parts A, B and C:"),
    lines(4),

    p("The fix (a coder does this with a mentor)", H2),
    p("In <b>PinpointTest.java</b>, at the end, un-comment this line so Pinpoint Test puts the motors back to BRAKE:"),
    p("<font face='Courier'>TestDriveMotors.setBrake(hardwareMap, true);</font>"),
    p("Rebuild, then repeat step B2 to show the problem is gone:"),
    Spacer(1, 4),
    table([["Step", "Setup", "Roll distance (in)", "Stops or coasts?"],
           ["B2 again", "Run Pinpoint Test (with the fix), then STOP, then manualDriver", "", ""]],
          [0.9 * inch, 3.5 * inch, 1.3 * inch, 1.3 * inch], row_height=0.5 * inch),

    p("Team decision", H2),
    p("What should we do about programs that never set brake mode (like manualDriver)? Check one:"),
    Spacer(1, 4),
    table([["", "Option"],
           ["", "manualDriver also sets BRAKE when it starts"],
           ["", "Drivers stop using manualDriver and use <b>Main TeleOp</b> (it already sets BRAKE at INIT)"],
           ["", "Something else: _______________________________________________"]],
          [0.4 * inch, 6.6 * inch], row_height=0.38 * inch),
    Spacer(1, 6),
    p("<b>Why?</b>"),
    lines(3),
    Spacer(1, 6),
    p("<b>After the test:</b> a mentor records the decision in <font face='Courier'>TeamCode/docs/decisions.md</font>, "
      "the results go in GitHub issue #17, and this sheet (plus photos and the stopping video) goes into the portfolio "
      "entry <font face='Courier'>docs/portfolio/2026-09-26-brake-mode-experiment.md</font>. Great judging material: "
      "hypothesis, prediction, experiment, result, fix!", SMALL),
]

doc = SimpleDocTemplate(OUT, pagesize=letter, leftMargin=0.75 * inch, rightMargin=0.75 * inch,
                        topMargin=0.7 * inch, bottomMargin=0.8 * inch,
                        title="Brake Mode Experiment (issue #17)", author="WMS Irondogs")
doc.build(story, onFirstPage=footer, onLaterPages=footer)
print("Wrote", OUT)
