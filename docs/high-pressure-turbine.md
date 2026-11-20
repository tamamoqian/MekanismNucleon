# 高压蒸汽涡轮（High-Pressure Steam Turbine）

在 Mekanism 工业涡轮的基础上，用本模组的高压外壳 / 端口（阀门）/ 排气口 / 冷凝器
替换对应元件，分压层继续使用 Mekanism 原本的分压元件；结构规则与 GUI 全部沿用工业涡轮。
**同体积下蒸汽容量、能量容量、蒸汽吞吐量和发电量都是工业涡轮的 2 倍。**

## 1. 方块对照

| 本模组方块 | 对应工业涡轮方块 | 作用 |
|---|---|---|
| 高压蒸汽涡轮外壳 `high_pressure_steam_turbine_casing` | 涡轮外壳 | 结构外壳，棱边必须使用它 |
| 高压蒸汽涡轮端口 `high_pressure_steam_turbine_port` | 涡轮阀门 | 输入蒸汽 + 输出能量（接蒸汽管与电缆） |
| 高压涡轮排气口 `high_pressure_steam_turbine_vent` | 涡轮排气口 | 输出冷凝水（接水管），必须在复合旋钮装置层或更高 |
| 高压蒸汽冷凝器 `high_pressure_steam_condenser` | 饱和冷凝器 | 放在复合旋钮装置上方，把蒸汽冷凝成水 |
| ——（Mekanism 原方块） | 分压元件 `mekanism:pressure_disperser` | 铺在复合旋钮装置周围的同一层 |
| ——（Mekanism 原方块） | 涡轮转子 / 涡轮叶片 / 复合旋钮装置 / 电磁线圈 | 内部核心部件保持不变 |

## 2. 结构规则（与工业涡轮一致）

- 尺寸：长/宽 5～17 且必须是**奇数**，高 3～18。
- 棱边（12 条边）必须用高压外壳；墙面（含顶面/底面）可以放外壳、端口或排气口。
- 内部只允许：空气、Mekanism 分压元件、高压蒸汽冷凝器、Mekanism 的涡轮转子 /
  复合旋钮装置 / 电磁线圈。
- 复合旋钮装置全结构只能 1 个，必须在 X/Z 中心。
- 转子柱：复合旋钮装置正下方到底层上一格，必须连续全是涡轮转子。
- 分压元件：必须铺满复合旋钮装置所在层的内截面（中心格除外）。
- 电磁线圈：从复合旋钮装置正上方开始，必须彼此连通。
- 冷凝器：必须在复合旋钮装置层**以上**。
- 排气口：至少 1 个，必须在复合旋钮装置层或更高。
- 宽度约束：内半径 (min(长,宽)-3)/2 ≥ (复合体Y-最低Y+1)/4。

## 3. 搭建示例（5×5×6，最小可用）

```
y5  C C V C C   顶层外壳 + 高压涡轮排气口（出水）
y4  C N N N C   高压蒸汽冷凝器层
y3  C K K K C   Mekanism 电磁线圈层
y2  C D X D C   Mekanism 分压元件层（X=复合旋钮装置）
y1  C A R A C   中心涡轮转子（右击装 2 片涡轮叶片）
y0  C C P C C   底层外壳 + 高压蒸汽涡轮端口（进汽+出电）
```

C=高压外壳，V=排气口，N=高压冷凝器，K=电磁线圈，D=Mekanism 分压元件，
X=复合旋钮装置，R=Mekanism 涡轮转子，P=端口，A=空气。四壁 y1–y4 全部高压外壳。

右击任意外壳即可成型（也会每 10 tick 自动尝试成型）；成型失败时聊天栏会显示具体原因。

## 4. 运行公式（高压版）

设 h = 转子柱高度，L×W×H = 结构尺寸：

```
下部体积 lowerVolume = L × W × h
蒸汽容量 = 2 × lowerVolume × chemicalPerTank(64,000 mB)
能量容量 = 2 × L × W × H × energyCapacityPerVolume(16,000,000 FE)
分压元件数 = (L-2) × (W-2) - 1

蒸汽吞吐上限 = 2 × min( lowerVolume × 分压元件数 × 1,280
                        , 排气口数 × 32,000 )            (mB/t)
每 mB 发电量 = maxEnergyPerSteam(10) / 28 × min(叶片数, 线圈数 × 4)   (FE/mB)
```

每 tick：

```
rate = min(罐内存汽, 吞吐上限, 能量空位 / 每mB发电量) × (罐内存汽 / 蒸汽容量)
产出 = 每mB发电量 × rate      (FE/t，正好是工业涡轮的 2 倍)
耗汽 = rate                  (mB/t，正好是工业涡轮的 2 倍)
产水 = min(rate, 冷凝器数 × 2 × 64,000)   (mB/t)
```

输出：能量从高压端口自动推向相邻电缆；冷凝水从排气口自动推向相邻流体容器。

## 5. GUI

直接挂载 Mekanism 工业涡轮的容器与界面（`GeneratorsContainerTypes.INDUSTRIAL_TURBINE`），
包含蒸汽量表、能量条、流量条、能量页签、涡轮数据页签与排汽模式按钮。

## 6. 与工业涡轮的差异

| 项目 | 工业涡轮 | 高压蒸汽涡轮 |
|---|---|---|
| 蒸汽容量 | 1× | 2× |
| 能量容量 | 1× | 2× |
| 蒸汽吞吐上限 | 1× | 2× |
| 发电量 | 1× | 2× |
| 冷凝水产量 / 缓存 | 1× | 2× |
| 结构、GUI、转子 / 线圈体系 | —— | 完全相同 |

## 7. 实现说明

- `MeknucTurbineMultiblock`：独立的多方块管理器（`MultiblockManager`）。
- `MeknucTurbineValidator`：复制工业涡轮校验流程，替换外壳 / 端口 / 排气口 / 冷凝器，
  分压层仍校验 `mekanism:pressure_disperser`。
- `MeknucTurbineMultiblockData`：继承 `TurbineMultiblockData`，重写 `tick` 与容量 / 产出
  getter 实现 2 倍缩放；能量从端口输出、水从排气口输出。
- 方块实体：外壳 / 端口 / 排气口继承 `TileEntityTurbineCasing`，冷凝器继承
  `TileEntityInternalMultiblock`，因此可以直接复用工业涡轮的 GUI 与渲染。

## 8. 测试记录

- `compileJava` / `build` 通过。
- 专用服务器启动无异常。
- 通过 RCON 搭建 5×5×6 结构（1 转子+2 叶片、8 个 Mekanism 分压元件、9 线圈、
  9 冷凝器、1 端口、1 排气口），4 秒内成功成型（外壳 NBT 出现 `inventory_id`）。
