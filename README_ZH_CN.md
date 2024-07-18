# DailyShop

[English](README.md) | 简体中文

# 介绍

一个受 [DailyShop](https://github.com/divios/DailyShop) 启发的 SpigotMC 插件。追求超级可自定性和效率。

## 文档

你可以在 [这里](https://docs.encmys.cn/s/ykdz-plugin-docs) 找到插件文档。

## 代办事项

- [x] 基于市场需求的动态定价
- [ ] 基岩版菜单
- [x] 商店商品总量
- [x] 交易日志
- [x] SQL 支持
- [ ] 随机数量以及基于数量的定价
- [ ] 根据条件判断是否要上架某个商品
- [ ] 打折
- [ ] 手动指定商店刷新结果
- [ ] 限购
- [ ] 购物车
- [ ] 商人（商店拥有余额）

## API

### Maven

```
<repositories>
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io/</url>
    </repository>
</repositories>
```

```
<dependencies>
    <dependency>
        <groupId>cn.encmys</groupId>
        <artifactId>DailyShop</artifactId>
        <version>{VERSION}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Gradle (Kotlin)

```kotlin
repositories {
    maven("https://jitpack.io/")
}
```

```kotlin
dependencies {
    compileOnly("cn.encmys:DailyShop:{VERSION}")
}
```

## 感谢

- [InvUI](https://github.com/NichtStudioCode/InvUI)
- [CommandAPI](https://github.com/JorelAli/CommandAPI)
- [ItemsLangAPI](https://github.com/Rubix327/ItemsLangAPI)

让这个插件成为可能。