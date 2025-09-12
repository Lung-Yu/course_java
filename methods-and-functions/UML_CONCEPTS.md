# 方法定義與呼叫 - UML概念圖

## 📊 模組概念關係圖

```mermaid
graph TB
    subgraph "方法基礎概念"
        A[方法定義] --> B[方法調用]
        A --> C[參數列表]
        A --> D[回傳類型]
        B --> E[參數傳遞]
    end
    
    subgraph "參數傳遞機制"
        E --> F[值傳遞]
        E --> G[引用傳遞]
        F --> H[基本型別]
        G --> I[物件引用]
        G --> J[陣列引用]
    end
    
    subgraph "進階方法概念"
        B --> K[方法多載]
        K --> L[參數數量不同]
        K --> M[參數類型不同]
        K --> N[參數順序不同]
        
        A --> O[可變參數]
        O --> P[Varargs語法]
        
        A --> Q[遞歸方法]
        Q --> R[基礎情況]
        Q --> S[遞歸情況]
        Q --> T[遞歸優化]
    end
    
    subgraph "最佳實踐"
        A --> U[命名規範]
        A --> V[參數驗證]
        A --> W[異常處理]
        A --> X[效能考量]
    end
    
    style A fill:#ff9999
    style B fill:#99ccff
    style K fill:#99ff99
    style Q fill:#ffcc99
```

## 🔄 學習流程圖

```mermaid
flowchart TD
    Start([開始學習方法]) --> Basic[掌握基本語法]
    Basic --> Params[理解參數傳遞]
    Params --> Return[掌握回傳值]
    Return --> Overload[學習方法多載]
    Overload --> Varargs[可變參數應用]
    Varargs --> Recursion[遞歸概念]
    Recursion --> BestPractice[最佳實踐]
    BestPractice --> Advanced[進階應用]
    Advanced --> End([完成學習])
    
    %% 檢查點
    Basic --> Check1{能否定義方法?}
    Check1 -->|否| Basic
    Check1 -->|是| Params
    
    Params --> Check2{理解參數傳遞?}
    Check2 -->|否| Params
    Check2 -->|是| Return
    
    Overload --> Check3{掌握多載規則?}
    Check3 -->|否| Overload
    Check3 -->|是| Varargs
    
    Recursion --> Check4{理解遞歸原理?}
    Check4 -->|否| Recursion
    Check4 -->|是| BestPractice
    
    style Start fill:#e1f5fe
    style End fill:#c8e6c9
    style Check1 fill:#fff3e0
    style Check2 fill:#fff3e0
    style Check3 fill:#fff3e0
    style Check4 fill:#fff3e0
```

## 🎯 知識點依賴關係

```mermaid
graph LR
    subgraph "前置知識"
        A1[Java基本語法]
        A2[變數與資料型別]
        A3[控制結構]
    end
    
    subgraph "核心概念"
        B1[方法定義語法]
        B2[參數與回傳值]
        B3[作用域概念]
    end
    
    subgraph "進階技巧"
        C1[方法多載]
        C2[可變參數]
        C3[遞歸設計]
    end
    
    subgraph "實踐應用"
        D1[錯誤處理]
        D2[效能優化]
        D3[程式碼重構]
    end
    
    A1 --> B1
    A2 --> B2
    A3 --> B3
    
    B1 --> C1
    B2 --> C2
    B3 --> C3
    
    C1 --> D1
    C2 --> D2
    C3 --> D3
    
    style A1 fill:#ffebee
    style B1 fill:#e8f5e8
    style C1 fill:#e3f2fd
    style D1 fill:#fce4ec
```

## 📈 複雜度與應用層次

```mermaid
pyramid TB
    subgraph "高級應用 (Advanced)"
        Level4[遞歸演算法<br/>效能優化<br/>設計模式]
    end
    
    subgraph "中級應用 (Intermediate)"  
        Level3[方法多載<br/>可變參數<br/>異常處理]
    end
    
    subgraph "基礎應用 (Basic)"
        Level2[參數傳遞<br/>回傳值處理<br/>作用域管理]
    end
    
    subgraph "入門概念 (Foundation)"
        Level1[方法定義<br/>方法調用<br/>基本語法]
    end
    
    Level1 --> Level2
    Level2 --> Level3  
    Level3 --> Level4
    
    style Level1 fill:#c8e6c9
    style Level2 fill:#81c784
    style Level3 fill:#4caf50
    style Level4 fill:#2e7d32
```

## 🔗 與其他模組的關聯

```mermaid
graph TD
    Methods[M01: 方法定義與呼叫] 
    
    %% 輸出到其他模組
    Methods --> String[M02: 字串處理<br/>方法的實際應用]
    Methods --> Collections[M03: 陣列與集合<br/>集合操作方法]
    Methods --> OOP[M04: 物件導向<br/>類別中的方法]
    Methods --> Algorithms[M05: 演算法<br/>演算法實作方法]
    Methods --> Generics[M06: 泛型<br/>泛型方法設計]
    
    %% 前置需求
    Basic[Java基本語法] --> Methods
    Variables[變數與型別] --> Methods
    Control[控制結構] --> Methods
    
    %% 應用領域
    Methods --> WebDev[Web開發]
    Methods --> DesktopApp[桌面應用]
    Methods --> Enterprise[企業級開發]
    
    style Methods fill:#ff9999
    style String fill:#99ccff
    style Collections fill:#99ff99
    style OOP fill:#ffcc99
    style Algorithms fill:#cc99ff
    style Generics fill:#ff99cc
```