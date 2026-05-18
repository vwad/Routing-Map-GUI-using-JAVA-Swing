package GUI.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class CalendarPanel extends JPanel {
    private LocalDate selected;
    private YearMonth displayed;
    private final JLabel monthLabel;
    private final JPanel daysPanel;
    private ActionListener routeAction;
    private ActionListener heatAction;

    public void setRouteAction(ActionListener routeAction)
    {
        this.routeAction = routeAction;
        //redraw calendar
        drawCalendar();
    }

    public void setHeatAction(ActionListener heatAction)
    {
        this.heatAction = heatAction;
        //redraw calendar
        drawCalendar();
    }




    public CalendarPanel() {
        selected = LocalDate.now();
        displayed = YearMonth.from(selected);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(180, 160));
        monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setFont(monthLabel.getFont().deriveFont(12f));
        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        prev.setFont(prev.getFont().deriveFont(12f));
        next.setFont(next.getFont().deriveFont(12f));
        prev.setFocusable(false);
        next.setFocusable(false);
        prev.setBackground(new Color(255, 255, 255));
        next.setBackground(new Color(255, 255, 255));
        prev.setMargin(new Insets(1, 1, 1, 1));
        next.setMargin(new Insets(1, 1, 1, 1));
        prev.addActionListener(e -> changeMonth(-1));
        next.addActionListener(e -> changeMonth(1));
        JPanel nav = new JPanel(new BorderLayout());
        nav.add(prev, BorderLayout.WEST);
        nav.add(monthLabel, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);
        add(nav, BorderLayout.NORTH);

        daysPanel = new JPanel(new GridLayout(0, 7));
        daysPanel.setBorder(new EmptyBorder(1, 1, 1, 1));
        add(daysPanel, BorderLayout.CENTER);
        drawCalendar();
    }

    private void changeMonth(int delta) {
        displayed = displayed.plusMonths(delta);
        drawCalendar();
    }

    private void drawCalendar() {
        daysPanel.removeAll();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMM yyyy");
        monthLabel.setText(displayed.format(df));
        LocalDate first = displayed.atDay(1);
        int startDow = first.getDayOfWeek().getValue()-1;
        int length = displayed.lengthOfMonth();
        String[] headers = {"M", "T", "W", "T", "F", "S", "S"};
        for (String h : headers) {
            JLabel lbl = new JLabel(h, JLabel.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(10f).deriveFont(Font.BOLD));
            daysPanel.add(lbl);
        }
        if (startDow != 7) {
            for (int i = 0; i < startDow; i++) {
                daysPanel.add(new JLabel(""));
            }
        }
        for (int day = 1; day <= length; day++) {
            LocalDate thisDate = displayed.atDay(day);
            JButton btn = new JButton(String.valueOf(day));
            btn.addActionListener(routeAction);
            btn.addActionListener(heatAction);
            btn.addActionListener(e -> setDate(thisDate));
            btn.setFont(btn.getFont().deriveFont(10f));
            btn.setMargin(new Insets(1, 1, 1, 1));
            btn.setBackground(new Color(255, 255, 255));
            if (thisDate.equals(selected)) {
                btn.setBackground(new Color(100, 255, 100));
            }
            daysPanel.add(btn);
        }
        int total = startDow + length;
        int blanks = (7 - (total % 7)) % 7;
        for (int i = 0; i < blanks; i++) {
            daysPanel.add(new JLabel(""));
        }
        daysPanel.revalidate();
        daysPanel.repaint();
    }

    public LocalDate getSelectedDate() {
        return selected;
    }

    private void setDate(LocalDate d) {
        selected = d;
        displayed = YearMonth.from(d);
        drawCalendar();
    }
}
