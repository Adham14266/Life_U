package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.data.local.FinanceTransaction
import com.example.ui.validation.isNonBlank
import com.example.ui.validation.isNonNegativeNumber
import com.example.ui.validation.isPositiveNumber
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancesScreen(viewModel: MainViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val scrollState = rememberScrollState()

    var transTitle by remember { mutableStateOf("") }
    var transAmount by remember { mutableStateOf("") }
    var transCategory by remember { mutableStateOf("Food") }
    var transTitleError by remember { mutableStateOf<String?>(null) }
    var transAmountError by remember { mutableStateOf<String?>(null) }

    // Dialog state for editing income and budget
    var tempIncome by remember { mutableStateOf(viewModel.monthlyIncome.toString()) }
    var tempBudget by remember { mutableStateOf(viewModel.monthlyBudgetLimit.toString()) }
    var tempIncomeError by remember { mutableStateOf<String?>(null) }
    var tempBudgetError by remember { mutableStateOf<String?>(null) }

    // Sync temp variables when the edit dialog is shown
    LaunchedEffect(viewModel.showEditBudgetDialog) {
        if (viewModel.showEditBudgetDialog) {
            tempIncome = viewModel.monthlyIncome.toString()
            tempBudget = viewModel.monthlyBudgetLimit.toString()
        }
    }

    // Dynamic aggregates
    val totalIncomeTransactions = transactions.filter { it.amount > 0 }.sumOf { it.amount }
    val totalExpensesTransactions = transactions.filter { it.amount < 0 }.sumOf { Math.abs(it.amount) }

    val totalIncome = viewModel.monthlyIncome + totalIncomeTransactions
    val totalExpenses = totalExpensesTransactions
    val totalBalance = totalIncome - totalExpenses

    val budgetLimit = viewModel.monthlyBudgetLimit
    val remainingBudget = Math.max(0.0, budgetLimit - totalExpenses)

    // Dynamic categories calculation
    val foodSum = transactions.filter { it.category == "Food" && it.amount < 0 }.sumOf { Math.abs(it.amount) }
    val housingSum = transactions.filter { it.category == "Housing" && it.amount < 0 }.sumOf { Math.abs(it.amount) }
    val transportSum = transactions.filter { (it.category == "Transportation" || it.category == "Transport") && it.amount < 0 }.sumOf { Math.abs(it.amount) }
    val booksOtherSum = transactions.filter { (it.category == "Books" || it.category == "Other" || it.category == "Entertainment") && it.amount < 0 }.sumOf { Math.abs(it.amount) } +
            transactions.filter { it.category != "Food" && it.category != "Housing" && it.category != "Transportation" && it.category != "Books" && it.category != "Other" && it.category != "Entertainment" && it.category != "Income" && it.amount < 0 }.sumOf { Math.abs(it.amount) }

    val sumExpenses = foodSum + housingSum + transportSum + booksOtherSum
    
    // Percentages for Donut Chart
    val foodPct = if (sumExpenses > 0) foodSum / sumExpenses else 0.0
    val housingPct = if (sumExpenses > 0) housingSum / sumExpenses else 0.0
    val transportPct = if (sumExpenses > 0) transportSum / sumExpenses else 0.0
    val booksOtherPct = if (sumExpenses > 0) booksOtherSum / sumExpenses else 0.0

    val foodAngle = (foodPct * 360f).toFloat()
    val housingAngle = (housingPct * 360f).toFloat()
    val transportAngle = (transportPct * 360f).toFloat()
    val booksOtherAngle = (booksOtherPct * 360f).toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Balance & Budget Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(GradientPrimaryStart, GradientCoolEnd)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    IconButton(
                        onClick = { viewModel.showEditBudgetDialog = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Budget & Income",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = String.format("$%,.2f", totalBalance),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Monthly Income Target",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = String.format("$%,.2f", totalIncome),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Monthly Budget Limit",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = String.format("$%,.2f", budgetLimit),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color.White.copy(alpha = 0.15f))

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format("Remaining: $%,.2f", remainingBudget),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                if (totalExpenses > budgetLimit) ErrorRed.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = String.format("%.0f%% Spent", if (budgetLimit > 0) (totalExpenses / budgetLimit) * 100 else 0.0),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (totalExpenses > budgetLimit) Color.White else Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val budgetProgress = if (budgetLimit > 0) (totalExpenses / budgetLimit).toFloat() else 0f
                val isOverBudget = totalExpenses > budgetLimit
                LinearProgressIndicator(
                    progress = { Math.min(1.0f, budgetProgress) },
                    color = if (isOverBudget) ErrorRed else SecondaryGreenContainer,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Spending Canvas Donut Chart Bento Box
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .background(SurfaceLow, RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Spending Overview",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(110.dp)
                    ) {
                        val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 12.dp.toPx()
                            if (sumExpenses == 0.0) {
                                drawArc(
                                    color = outlineVariantColor.copy(alpha = 0.3f),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                            } else {
                                var currentStartAngle = 0f
                                if (foodAngle > 0f) {
                                    drawArc(
                                        color = PrimaryBlue,
                                        startAngle = currentStartAngle,
                                        sweepAngle = foodAngle,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                    currentStartAngle += foodAngle
                                }
                                if (housingAngle > 0f) {
                                    drawArc(
                                        color = TertiaryViolet,
                                        startAngle = currentStartAngle,
                                        sweepAngle = housingAngle,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                    currentStartAngle += housingAngle
                                }
                                if (transportAngle > 0f) {
                                    drawArc(
                                        color = SecondaryGreen,
                                        startAngle = currentStartAngle,
                                        sweepAngle = transportAngle,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                    currentStartAngle += transportAngle
                                }
                                if (booksOtherAngle > 0f) {
                                    drawArc(
                                        color = Color(0xFFFF9800),
                                        startAngle = currentStartAngle,
                                        sweepAngle = booksOtherAngle,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Total Spent",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format("$%,.0f", totalExpenses),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Legend indicators
            Column(
                modifier = Modifier.weight(0.9f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LegendRowItem(color = PrimaryBlue, label = "Food", pct = String.format("%.0f%% ($%,.0f)", foodPct * 100, foodSum))
                LegendRowItem(color = TertiaryViolet, label = "Housing", pct = String.format("%.0f%% ($%,.0f)", housingPct * 100, housingSum))
                LegendRowItem(color = SecondaryGreen, label = "Transport", pct = String.format("%.0f%% ($%,.0f)", transportPct * 100, transportSum))
                LegendRowItem(color = Color(0xFFFF9800), label = "Books/Other", pct = String.format("%.0f%% ($%,.0f)", booksOtherPct * 100, booksOtherSum))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Transaction History List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transaction History",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = { viewModel.showAddTransactionDialog = true },
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Transaction", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions logged yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (trans in transactions) {
                    TransactionRowItem(
                        transaction = trans,
                        onDelete = {
                            viewModel.deleteTransaction(trans)
                        }
                    )
                }
            }
        }
    }

    // Edit Budget Settings Dialog
    if (viewModel.showEditBudgetDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showEditBudgetDialog = false },
            title = { Text("Edit Budget Settings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = tempIncome,
                        onValueChange = {
                            tempIncome = it
                            tempIncomeError = null
                        },
                        label = { Text("Base Monthly Income ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        isError = tempIncomeError != null,
                        supportingText = tempIncomeError?.let { error -> { Text(error) } }
                    )

                    OutlinedTextField(
                        value = tempBudget,
                        onValueChange = {
                            tempBudget = it
                            tempBudgetError = null
                        },
                        label = { Text("Monthly Expense Budget Limit ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        isError = tempBudgetError != null,
                        supportingText = tempBudgetError?.let { error -> { Text(error) } }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val inc = tempIncome.trim().toDoubleOrNull()
                    val bud = tempBudget.trim().toDoubleOrNull()
                    val validIncome = inc != null && isNonNegativeNumber(tempIncome)
                    val validBudget = bud != null && isNonNegativeNumber(tempBudget)

                    tempIncomeError = if (!validIncome) "Enter a valid income amount" else null
                    tempBudgetError = if (!validBudget) "Enter a valid budget amount" else null

                    if (validIncome && validBudget) {
                        viewModel.updateBudgetSettings(inc, bud)
                        viewModel.showEditBudgetDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showEditBudgetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Transaction Dialog
    if (viewModel.showAddTransactionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showAddTransactionDialog = false },
            title = { Text("Log Transaction", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = transTitle,
                        onValueChange = {
                            transTitle = it
                            transTitleError = null
                        },
                        label = { Text("Transaction Description") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = transTitleError != null,
                        supportingText = transTitleError?.let { error -> { Text(error) } }
                    )

                    OutlinedTextField(
                        value = transAmount,
                        onValueChange = {
                            transAmount = it
                            transAmountError = null
                        },
                        label = { Text("Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        isError = transAmountError != null,
                        supportingText = transAmountError?.let { error -> { Text(error) } }
                    )

                    // Category dropdown helper
                    Text("Category", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Food", "Housing", "Transportation").forEach { cat ->
                                val isSelected = transCategory == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary else SurfaceNormal,
                                            shape = CircleShape
                                        )
                                        .clickable { transCategory = cat }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Books", "Entertainment", "Other", "Income").forEach { cat ->
                                val isSelected = transCategory == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary else SurfaceNormal,
                                            shape = CircleShape
                                        )
                                        .clickable { transCategory = cat }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val amt = transAmount.trim().toDoubleOrNull()
                    val validTitle = isNonBlank(transTitle)
                    val validAmount = amt != null && isPositiveNumber(transAmount)

                    transTitleError = if (!validTitle) "Transaction description is required" else null
                    transAmountError = if (!validAmount) "Enter an amount greater than 0" else null

                    if (validTitle && validAmount) {
                        viewModel.addTransaction(transTitle.trim(), amt, transCategory)
                        transTitle = ""
                        transAmount = ""
                        viewModel.showAddTransactionDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showAddTransactionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LegendRowItem(color: Color, label: String, pct: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = pct,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TransactionRowItem(
    transaction: FinanceTransaction,
    onDelete: () -> Unit
) {
    val isIncome = transaction.amount > 0
    val colorBadge = if (isIncome) SecondaryGreen else ErrorRed
    val prefixText = if (isIncome) "+" else "-"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceLowest, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Icon matching category
        val iconType = when (transaction.category) {
            "Income" -> Icons.Default.TrendingUp
            "Food" -> Icons.Default.Restaurant
            "Books" -> Icons.Default.Book
            "Transportation", "Transport" -> Icons.Default.DirectionsBus
            "Housing" -> Icons.Default.Home
            "Entertainment" -> Icons.Default.Movie
            else -> Icons.Default.ReceiptLong
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(colorBadge.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = iconType, contentDescription = null, tint = colorBadge, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = transaction.dateText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = String.format("$Prefix$%,.2f", prefixText, Math.abs(transaction.amount)),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = colorBadge
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Transaction",
                tint = ErrorRed.copy(alpha = 0.8f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private const val Prefix = "%s"
