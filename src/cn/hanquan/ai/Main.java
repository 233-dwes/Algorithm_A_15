package cn.hanquan.ai;

/**
 * 设计一个启发函数,利用A*算法求解15数码问题
 * 
 * 要求: 尽可能用与A*算法一致的思路实现算法, 力求简单明了地给出一个解. 
 * 		1)打印并上交带有关键步骤说明的程序代码.
 * 		2)演示运行过程,并回答老师的询问
 * @author luyang.gong
 * 
 * f(n)=g(n)+h(n)
 * g(n):已经走过
 * h(n):对未来的估计
 * 
 * 
	Begin： 
	读入初始状态和目标状态，并计算初始状态评价函数值f； 
	初始化两个open表和closed表，将初始状态放入open表中
	If（open表为空）
	    查找失败；
	End if
	else  
	①	在open表中找到评价值最小的节点，作为当前结点，并放入closed表中； 
	②	判断当前结点状态和目标状态是否一致，若一致，跳出循环；否则跳转到③； 
	③	对当前结点，分别按照上、下、左、右方向移动空格位置来扩展新的状态结点，并计算新扩展结点的评价值f并记录其父节点； 
	④	对于新扩展的状态结点，进行如下操作：
		A．新节点既不在open表中，也不在closed表中，则ADD (mj, OPEN)；
		B．新节点在open表中，则	IF f(n-mk) < f(mk)  
								THEN f(mk):=f(n-mk)， 
		C．新节点在closed表中，则IF f(n-ml) < f(ml)  
								THEN f(ml):=f(n-ml)，     
	⑤	把当前结点从open表中移除； 
	End if
	End
 *
 */
public class Main {
	public static Board beginBoard = new Board(Init.BEGINARR);
	public static Board endBoard = new Board(Init.ENDARR);

	public static void main(String[] args) {

		OpenTable openTable = new OpenTable();
		openTable.tbArr.add(beginBoard);// 这里需要拷贝逻辑 否则复制的只是指针
		CloseTable closeTable = new CloseTable();

		System.out.println(openTable);
		Board curBoard = null;

		int count = 0;

		while (openTable.tbArr.size() != 0) {
			System.out.println("第" + ++count + "次拓展");
			openTable.sortItSelf();
			curBoard = openTable.getMinBoard();
			closeTable.tbArr.add(curBoard);

			// 是否为end
			if (curBoard.equals(endBoard)) {
				System.out.println("SUCCESS:" + curBoard);
				while (curBoard.parentBoard != null) {
					System.out.println("上一步：");
					System.out.println(curBoard.parentBoard);
					curBoard = curBoard.parentBoard;
				}
				break;
			}
			// 4方向拓展
			for (int s = 0; s < 4; s++) {
				Board newBoard = null;
				if (s == 0) {
					if (curBoard.canDown()) {
						newBoard = curBoard.goDown();
					} else {
						continue;
					}
				} else if (s == 1) {
					if (curBoard.canUp()) {
						newBoard = curBoard.goUp();
					} else {
						continue;
					}
				} else if (s == 2) {
					if (curBoard.canRight()) {
						newBoard = curBoard.goRight();
					} else {
						continue;
					}
				} else if (s == 3) {
					if (curBoard.canLeft()) {
						newBoard = curBoard.goLeft();
					} else {
						continue;
					}
				}

				if (openTable.hasBoard(newBoard)) {// 新节点在open表中 比较已有fn和新节点fn的大小
					Board oldBoard = openTable.getBoardByArr(newBoard.arr);
					if (newBoard.fn < oldBoard.fn) {// 新的比旧的fn小
						// 更新指针
						newBoard.childBoards = oldBoard.childBoards;
						for (int i = 0; i < newBoard.childBoards.size(); i++) {
							newBoard.childBoards.get(i).parentBoard = newBoard;
						}
						// 删旧的 增新的
						openTable.tbArr.remove(openTable.getIndex(oldBoard));
						openTable.tbArr.add(newBoard);
						System.out.println("openTable:update");
					}
				} else if (closeTable.hasBoard(newBoard)) {// 新节点在close表中
					Board oldBoard = closeTable.getBoardByArr(newBoard.arr);
					if (newBoard.fn < oldBoard.fn) {
						newBoard.childBoards = oldBoard.childBoards;
						for (int i = 0; i < newBoard.childBoards.size(); i++) {
							newBoard.childBoards.get(i).parentBoard = newBoard;
						}
						closeTable.tbArr.remove(closeTable.getIndex(oldBoard));
//						closeTable.tbArr.add(newBoard);
						openTable.tbArr.add(newBoard);// 重新放回open表中
						System.out.println("openTable:update");
					}
				} else {// 两表都不在
					openTable.tbArr.add(newBoard);
					System.out.println("openTable:add");
				}
			}
			openTable.tbArr.remove(openTable.getIndex(curBoard));
		}
	}
}
