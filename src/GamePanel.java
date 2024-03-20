import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    // 屏幕宽度、高度和单元大小
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    // 游戏单元总数
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    // 动画延迟时间（影响游戏速度）
    static final int DELAY = 75;
    // 存储蛇身体部分的位置
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    // 蛇的初始身体部分数
    int bodyParts = 6;
    // 吃到的苹果数
    int applesEaten;
    // 苹果的位置
    int appleX;
    int appleY;
    // 蛇移动的方向
    char direction = 'R';
    // 游戏是否运行
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        // 设置面板大小和背景颜色
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        // 添加键盘监听器
        this.addKeyListener(new MyKeyAdapter());
        // 开始游戏
        startGame();
    }

    // 开始游戏的方法
    public void startGame() {
        // 创建新苹果
        newApple();
        running = true;
        // 设置计时器来控制游戏速度和动画
        timer = new Timer(DELAY, this);
        timer.start();
    }

    // 绘制组件的方法
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 调用绘制方法
        draw(g);
    }

    // 绘制游戏中的所有元素
    public void draw(Graphics g) {
        if (running) {
            // 绘制网格
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            // 绘制苹果
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // 绘制蛇
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    // 蛇头
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    // 蛇身
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            // 显示得分
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            // 游戏结束时显示的方法
            gameOver(g);
        }
    }

    // 创建新苹果的方法
    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    // 蛇移动的方法
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // 根据方向移动蛇头
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    // 检查蛇是否吃到苹果
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++; // 增加蛇的身体长度
            applesEaten++; // 增加吃到的苹果数量
            newApple(); // 生成新的苹果
        }
    }

    // 检查碰撞
    public void checkCollisions() {
        // 检查蛇是否碰到身体
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        // 检查蛇是否触碰左边界
        if (x[0] < 0) {
            running = false;
        }
        // 检查蛇是否触碰右边界
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }
        // 检查蛇是否触碰上边界
        if (y[0] < 0) {
            running = false;
        }
        // 检查蛇是否触碰下边界
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        // 如果蛇碰到边界或自己，停止游戏
        if (!running) {
            timer.stop();
        }
    }

    // 游戏结束时显示的画面
    public void gameOver(Graphics g) {
        // 显示得分
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        // 显示游戏结束文字
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    // 键盘操作适配器类
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') { // 防止蛇直接向反方向移动
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
